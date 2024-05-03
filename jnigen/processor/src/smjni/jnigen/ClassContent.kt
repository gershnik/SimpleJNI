/*
 Copyright 2014 Smartsheet Inc.
 Copyright 2019 SmJNI Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package smjni.jnigen

import javax.lang.model.element.*
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

internal class NativeMethod(val isStatic: Boolean,
                            var isNameNonUnique: Boolean,
                            val returnType: String,
                            val name: CharSequence,
                            val arguments: List<Pair<String, String?>>)

internal enum class JavaEntityType {
    Constructor,
    Method,
    StaticMethod,
    Field,
    StaticField
}

internal class JavaEntity(val type: JavaEntityType,
                          val isFinal: Boolean,
                          val allowNonVirt: Boolean,
                          var name: UniqueName,
                          val templateArguments: List<String>,
                          val returnType: String,
                          val argTypes: List<String>,
                          val argNames: List<String>)

internal class ClassContent(val classElement: TypeElement,
                            val binaryName: String,
                            val cppClassName: String,
                            val convertsTo: Set<String>,
                            typeMap: TypeMap,
                            ctxt: Context) {

    private val m_nativeMethods = ArrayList<NativeMethod>()
    private val m_javaEntities = ArrayList<JavaEntity>()

    private val CALLED_BY_NATIVE = ctxt.calledByNativeAnnotation
    private val CTOR_NAME = ctxt.ctorName

    internal val cppName = typeMap.nativeNameOf(classElement.qualifiedName)

    init {

        val previousNativeNameUsers = HashMap<Name, NativeMethod>()
        val names = NameTable()

        for (childElement in classElement.enclosedElements) {

            try {
                when (childElement.kind) {
                    ElementKind.METHOD -> {

                        val methodElement = childElement as ExecutableElement

                        if (childElement.modifiers.contains(Modifier.NATIVE))
                            addNativeMethod(methodElement, previousNativeNameUsers, typeMap)

                        val annotation = childElement.annotationMirrors.find {
                            val annotationType = it.annotationType.asElement() as TypeElement
                            annotationType.qualifiedName.contentEquals(CALLED_BY_NATIVE)
                        }
                        if (annotation != null) {
                            var allowNonVirt = false
                            for ((name, value) in ctxt.elementUtils.getElementValuesWithDefaults(annotation)) {

                                when {
                                    name.simpleName.contentEquals("allowNonVirtualCall") -> allowNonVirt = value.value as Boolean
                                }
                            }

                            addJavaMethod(methodElement, allowNonVirt, names, typeMap)

                        }
                    }
                    ElementKind.ENUM_CONSTANT, ElementKind.FIELD -> {

                        val fieldElement = childElement as VariableElement

                        if (childElement.annotationMirrors.any {
                                    val annotationType = it.annotationType.asElement() as TypeElement
                                    annotationType.qualifiedName.contentEquals(CALLED_BY_NATIVE)
                                }) {
                            addJavaField(fieldElement, names, typeMap)
                        }
                    }
                    ElementKind.CONSTRUCTOR -> {

                        val constructorElement = childElement as ExecutableElement

                        if (childElement.annotationMirrors.any {
                                    val annotationType = it.annotationType.asElement() as TypeElement
                                    annotationType.qualifiedName.contentEquals(CALLED_BY_NATIVE)
                                }) {
                            addJavaConstructor(constructorElement, names, typeMap)
                        }
                    }
                    else -> {
                    }
                }
            } catch (ex: ProcessingException) {
                ex.element = childElement
                throw ex
            }
        }

    }

    internal val nativeMethods: List<NativeMethod>
        get() = m_nativeMethods

    internal val javaEntities: List<JavaEntity>
        get() = m_javaEntities

    internal val hasCppClass: Boolean
        get() = javaEntities.isNotEmpty() || nativeMethods.isNotEmpty()


    private fun isPrimitiveReturnType(type: TypeMirror): Boolean {

        return type.kind.isPrimitive || type.kind == TypeKind.VOID
    }

    private fun addNativeMethod(methodElement: ExecutableElement,
                                previousNameUsers: MutableMap<Name, NativeMethod>, typeMap: TypeMap)  {


        val isStatic = methodElement.modifiers.contains(Modifier.STATIC)
        val returnType = typeMap.nativeNameOf(methodElement.returnType)
        val methodName = methodElement.simpleName
        val previousNameUser = previousNameUsers[methodName]
        val isNameNonUnique: Boolean
        if (previousNameUser != null) {
            previousNameUser.isNameNonUnique = true
            isNameNonUnique = true
        } else {
            isNameNonUnique = false
        }
        val arguments = ArrayList<Pair<String, String?>>()
        arguments.add(Pair("JNIEnv *", null))
        if (isStatic)
            arguments.add(Pair("jclass", null))
        else
            arguments.add(Pair("$cppName", null))
        methodElement.parameters.mapTo(arguments) {
            Pair(typeMap.nativeNameOf(it.asType()), it.simpleName.toString())
        }
        val method = NativeMethod(isStatic, isNameNonUnique, returnType, methodName, arguments)
        m_nativeMethods.add(method)
        previousNameUsers[methodName] = method
    }

    private fun addJavaMethod(methodElement: ExecutableElement,
                              allowNonVirt: Boolean,
                              names: NameTable,
                              typeMap: TypeMap) {

        val isStatic = methodElement.modifiers.contains(Modifier.STATIC)
        val methodName = names.allocateName(methodElement.simpleName.toString())

        val templateArguments = ArrayList<String>()
        val argTypes = ArrayList<String>()
        val argNames = ArrayList<String>()

        val baseReturnType = typeMap.nativeNameOf(methodElement.returnType)
        templateArguments.add(baseReturnType)
        val returnType = typeMap.wrapperNameOf(methodElement.returnType, false)
        templateArguments.add("$cppName")
        if (!isStatic) {
            argTypes.add(typeMap.wrapperNameOf(classElement.asType(), true))
            argNames.add("self")
        } 
        for (param in methodElement.parameters) {
            val paramType = param.asType()
            templateArguments.add(typeMap.nativeNameOf(paramType))
            argTypes.add(typeMap.wrapperNameOf(paramType, true))
            argNames.add(param.simpleName.toString())
        }
        val method = JavaEntity(if (isStatic) JavaEntityType.StaticMethod else JavaEntityType.Method,
                methodElement.modifiers.contains(Modifier.FINAL),
                if (isStatic) false else allowNonVirt,
                methodName, templateArguments, returnType, argTypes, argNames)
        m_javaEntities.add(method)
    }

    private fun addJavaField(fieldElement: VariableElement, names: NameTable, typeMap: TypeMap) {

        val isStatic = fieldElement.modifiers.contains(Modifier.STATIC)
        val fieldName = names.allocateName(fieldElement.simpleName.toString())

        val templateArguments = ArrayList<String>()
        val argTypes = ArrayList<String>()
        val argNames = ArrayList<String>()

        val fieldType = fieldElement.asType()
        templateArguments.add(typeMap.nativeNameOf(fieldType))
        val returnType = typeMap.wrapperNameOf(fieldType, false)
        templateArguments.add("$cppName")
        if (!isStatic) {
            argTypes.add(typeMap.wrapperNameOf(classElement.asType(), true))
            argNames.add("self")
        }

        argTypes.add(typeMap.wrapperNameOf(fieldType, true))

        val field = JavaEntity(if (isStatic) JavaEntityType.StaticField else JavaEntityType.Field,
                fieldElement.modifiers.contains(Modifier.FINAL),
                false,
                fieldName, templateArguments, returnType, argTypes, argNames)
        m_javaEntities.add(field)
    }

    private fun addJavaConstructor(constructorElement: ExecutableElement, names: NameTable, typeMap: TypeMap) {

        val name = names.allocateName(CTOR_NAME)

        val templateArguments = ArrayList<String>()
        val argTypes = ArrayList<String>()
        val argNames = ArrayList<String>()

        templateArguments.add("$cppName")
        val returnType = "smjni::local_java_ref<$cppName>"
        for (param in constructorElement.parameters) {
            val paramType = param.asType()
            templateArguments.add(typeMap.nativeNameOf(paramType))
            argTypes.add(typeMap.wrapperNameOf(paramType, true))
            argNames.add(param.simpleName.toString())
        }

        val ctor = JavaEntity(JavaEntityType.Constructor, false, false, name, templateArguments, returnType, argTypes, argNames)
        m_javaEntities.add(ctor)
    }

}
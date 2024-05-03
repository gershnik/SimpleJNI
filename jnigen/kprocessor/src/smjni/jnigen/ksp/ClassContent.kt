/*
 Copyright 2021 SmJNI Contributors

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

package smjni.jnigen.ksp

import com.google.devtools.ksp.KspExperimental

@KspExperimental
internal class ClassContent(classInfo: JavaClassInfo,
                            val cppClassName: String,
                            context: Context) {

    class NativeMethod(val isStatic: Boolean,
                       var isNameNonUnique: Boolean,
                       val returnType: String,
                       val name: String,
                       val arguments: List<Pair<String, String?>>) : Comparable<NativeMethod> {

        override fun compareTo(other: NativeMethod): Int {
            return compareValuesBy(this, other,
                { name },
                { lexicographicallyComparable(arguments, compareBy { it.first }) }
            )
        }
    }

    enum class JavaEntityType {
        Constructor,
        Method,
        StaticMethod,
        Field,
        StaticField
    }

    class JavaEntity(val type: JavaEntityType,
                     val isFinal: Boolean,
                     val allowNonVirt: Boolean,
                     val name: UniqueName,
                     val templateArguments: List<String>,
                     val returnType: String,
                     val argTypes: List<String>,
                     val argNames: List<String>) : Comparable<JavaEntity> {

        override fun compareTo(other: JavaEntity): Int {
            return compareValuesBy(this, other,
                { it.type },
                { it.name.original },
                { lexicographicallyComparable(it.argTypes) }
            )
        }
    }

    private val nameTable = NameTable()

    private val _nativeMethods = mutableListOf<NativeMethod>()
    val nativeMethods: List<NativeMethod>
        get() = _nativeMethods

    private val _javaEntities = mutableListOf<JavaEntity>()
    val javaEntities: List<JavaEntity>
        get() = _javaEntities

    val hasCppClass: Boolean
        get() = _javaEntities.isNotEmpty() || nativeMethods.isNotEmpty()

    val cppName = context.typeMap.nativeNameOf(classInfo.qualifiedJavaClassName)

    val javaName = classInfo.qualifiedJavaClassName

    val convertsTo = classInfo.convertsTo

    init {

        val previousNativeNameUsers = mutableMapOf<String, NativeMethod>()

        classInfo.getExposed(context).forEach {
            when (it) {
                is JavaClassInfo.NativeFunction -> addNativeMethod(it, previousNativeNameUsers, context)
                is JavaClassInfo.Function -> if (it.isConstructor) {
                        addJavaConstructor(it, context)
                    } else {
                        addJavaMethod(it, context)
                    }
                is JavaClassInfo.Getter -> addJavaGetter(it, context)
                is JavaClassInfo.Setter -> addJavaSetter(it, context)
                is JavaClassInfo.Field  -> addJavaField(it, context)
                is JavaClassInfo.EnumEntry -> addJavaEnumEntry(it, context)
            }
        }

        _javaEntities.sort()
        _nativeMethods.sort()
    }

    private fun addNativeMethod(functionInfo: JavaClassInfo.NativeFunction,
                                previousNameUsers: MutableMap<String, NativeMethod>,
                                context: Context)  {

        val callableInfo = functionInfo.getCallableInfo(context)

        val nativeReturnType = context.typeMap.nativeNameOf(callableInfo.returnType)
        val jvmName = callableInfo.jvmName
        val previousNameUser = previousNameUsers[jvmName]
        val isNameNonUnique: Boolean
        if (previousNameUser != null) {
            previousNameUser.isNameNonUnique = true
            isNameNonUnique = true
        } else {
            isNameNonUnique = false
        }

        val arguments = mutableListOf<Pair<String, String?>>(
            Pair("JNIEnv *", null),
            if (functionInfo.isStatic)
                Pair("jclass", null)
            else
                Pair(cppName, null)
        )

        val allParams = when(callableInfo.extensionReceiver) {
            null -> emptySequence()
            else -> sequenceOf(Pair(callableInfo.extensionReceiver, "receiver"))
        } + callableInfo.parameters.map { Pair(it.type, it.name?.asString() ?: "arg")}

        allParams.mapTo(arguments) { param ->
            val paramType = param.first.resolve()
            val paramTypeName = context.typeMap.nativeNameOf(paramType)
            val paramName = param.second
            Pair(paramTypeName, paramName)
        }

        val method = NativeMethod(functionInfo.isStatic, isNameNonUnique, nativeReturnType, jvmName, arguments)
        _nativeMethods.add(method)
        previousNameUsers[jvmName] = method
    }


    private fun addJavaCallable(callableInfo: JavaClassInfo.CallableInfo,
                                isConstructor: Boolean,
                                isStatic: Boolean,
                                isOpen: Boolean,
                                allowNonVirt: Boolean,
                                context: Context) {

        val methodName = nameTable.allocateName(if (isConstructor) context.ctorName else callableInfo.jvmName)

        val templateArguments = mutableListOf<String>()
        val argTypes = mutableListOf<String>()
        val argNames = mutableListOf<String>()

        val nativeReturnType: String
        if (isConstructor) {
            templateArguments.add(cppName)
            nativeReturnType = "smjni::local_java_ref<$cppName>"

        } else {
            val baseReturnTypeName = context.typeMap.nativeNameOf(callableInfo.returnType)
            templateArguments.add(baseReturnTypeName)
            nativeReturnType = context.typeMap.wrapperNameOf(baseReturnTypeName, false)
            templateArguments.add(cppName)
            if (!isStatic) {
                argTypes.add(context.typeMap.wrapperNameOf(cppName, true))
                argNames.add("self")
            }
        }

        val allParams = when(callableInfo.extensionReceiver) {
            null -> emptySequence()
            else -> sequenceOf(Pair(callableInfo.extensionReceiver, "receiver"))
        } + callableInfo.parameters.map { Pair(it.type, it.name?.asString() ?: "arg")}

        allParams.forEach { param ->
            val paramType = param.first.resolve()
            val paramTypeName = context.typeMap.nativeNameOf(paramType)
            templateArguments.add(paramTypeName)
            argTypes.add(context.typeMap.wrapperNameOf(paramTypeName, true))
            val paramName = param.second
            argNames.add(paramName)
        }

        val method = if (isConstructor)
                JavaEntity(JavaEntityType.Constructor, isFinal = false, allowNonVirt = false,
                    methodName, templateArguments, nativeReturnType, argTypes, argNames)
            else if (isStatic)
                JavaEntity(JavaEntityType.StaticMethod, isFinal = !isOpen, allowNonVirt = false,
                    methodName, templateArguments, nativeReturnType, argTypes, argNames)
            else
                JavaEntity(JavaEntityType.Method, isFinal = !isOpen, allowNonVirt,
                    methodName, templateArguments, nativeReturnType, argTypes, argNames)

        _javaEntities.add(method)
    }

    private fun addJavaField(fieldInfo: JavaClassInfo.Field, context: Context) {

        val isWritable = fieldInfo.isMutable
        val fieldName = nameTable.allocateName(fieldInfo.jvmName)

        val templateArguments = ArrayList<String>()
        val argTypes = ArrayList<String>()
        val argNames = ArrayList<String>()

        val fieldType = fieldInfo.type
        val fieldTypeName = context.typeMap.nativeNameOf(fieldType)
        templateArguments.add(fieldTypeName)
        val returnType = context.typeMap.wrapperNameOf(fieldTypeName, false)
        templateArguments.add(cppName)
        if (!fieldInfo.isStatic) {
            argTypes.add(context.typeMap.wrapperNameOf(cppName, true))
            argNames.add("self")
        }

        argTypes.add(context.typeMap.wrapperNameOf(fieldTypeName, true))

        val field = JavaEntity(
            if (fieldInfo.isStatic) JavaEntityType.StaticField else JavaEntityType.Field,
            !isWritable,
            false, //meaningless for fields
            fieldName, templateArguments, returnType, argTypes, argNames
        )
        _javaEntities.add(field)
    }

    private fun addJavaEnumEntry(enumEntryInfo: JavaClassInfo.EnumEntry, context: Context) {
        val fieldName = nameTable.allocateName(enumEntryInfo.jvmName)

        val templateArguments = ArrayList<String>()
        val argTypes = ArrayList<String>()
        val argNames = ArrayList<String>()

        val fieldType = enumEntryInfo.type
        val fieldTypeName = context.typeMap.nativeNameOf(fieldType)
        templateArguments.add(fieldTypeName)
        val returnType = context.typeMap.wrapperNameOf(fieldTypeName, false)
        templateArguments.add(cppName)

        argTypes.add(context.typeMap.wrapperNameOf(fieldTypeName, true))

        val field = JavaEntity(
            JavaEntityType.StaticField,
            true,
            false, //meaningless for fields
            fieldName, templateArguments, returnType, argTypes, argNames
        )
        _javaEntities.add(field)
    }

    private fun addJavaConstructor(constructorInfo: JavaClassInfo.Function, context: Context) {

        addJavaCallable(constructorInfo.getCallableInfo(context), isConstructor = true, isStatic = false, isOpen = false, allowNonVirt = false, context)
    }

    private fun addJavaMethod(functionInfo: JavaClassInfo.Function, context: Context) {
        val allowNonVirt = functionInfo.calledByNative.arguments.find {
            it.name?.getShortName() == "allowNonVirtualCall" }?.value as? Boolean ?: false

        addJavaCallable(functionInfo.getCallableInfo(context), isConstructor = false, functionInfo.isStatic, functionInfo.isOpen, allowNonVirt, context)
    }

    private fun addJavaGetter(getter: JavaClassInfo.Getter, context: Context) {

        val allowNonVirt = getter.calledByNative.arguments.find {
            it.name?.getShortName() == "allowNonVirtualCall" }?.value as? Boolean ?: false
        addJavaCallable(getter.getCallableInfo(context), isConstructor = false, getter.isStatic, getter.isOpen, allowNonVirt, context)
    }

    private fun addJavaSetter(setter: JavaClassInfo.Setter, context: Context) {

        val allowNonVirt = setter.calledByNative.arguments.find {
            it.name?.getShortName() == "allowNonVirtualCall" }?.value as? Boolean ?: false
        addJavaCallable(setter.getCallableInfo(context), false, setter.isStatic, setter.isOpen, allowNonVirt, context)
    }

}
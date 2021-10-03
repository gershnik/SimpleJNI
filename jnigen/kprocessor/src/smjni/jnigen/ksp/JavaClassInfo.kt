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
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.symbol.*

@KspExperimental
internal class JavaClassInfo private constructor(val qualifiedJavaClassName: String,
                                                 val convertsTo: Set<String>,
                                                 val annotations: Sequence<KSAnnotation>,
                                                 val node: KSNode) {

    class CallableInfo private constructor(val jvmName: String, val extensionReceiver: KSTypeReference?,
                                           val returnType: KSType, val parameters: List<KSValueParameter>) {

        constructor(context: Context, function: KSFunctionDeclaration) : this(
            context.getJvmName(function) ?: throw ProcessingException("Function ${function.simpleName.asString()} has no Java representation", function),
            function.extensionReceiver,
            function.returnType?.resolve() ?: throw ProcessingException("Return type of ${function.simpleName.asString()} not available", function),
            function.parameters
        )

        constructor(context: Context, getter: KSPropertyGetter) : this(
            context.getJvmName(getter) ?: throw ProcessingException("Getter for ${getter.receiver.simpleName.asString()} has no Java representation", getter.receiver),
            getter.receiver.extensionReceiver,
            getter.returnType?.resolve() ?: throw ProcessingException("Return type of ${getter.receiver.simpleName.asString()} not available", getter.receiver),
            emptyList()
        )

        constructor(context: Context, setter: KSPropertySetter) : this(
            context.getJvmName(setter) ?: throw ProcessingException("Setter for ${setter.receiver.simpleName.asString()} has no Java representation", setter.receiver),
            setter.receiver.extensionReceiver,
            context.builtIns.unitType,
            listOf(setter.parameter)
        )
    }

    sealed class ExposedEntity(val isStatic: Boolean)

    class NativeFunction(private val declaration: KSFunctionDeclaration, isStatic: Boolean) : ExposedEntity(isStatic) {

        fun getCallableInfo(context: Context) = CallableInfo(context, declaration)
    }
    class Function(private val declaration: KSFunctionDeclaration, val calledByNative: KSAnnotation, isStatic: Boolean) : ExposedEntity(isStatic) {

        fun getCallableInfo(context: Context) = CallableInfo(context, declaration)

        val isConstructor : Boolean
            get() = declaration.isConstructor()

        val isOpen : Boolean
            get() = declaration.isOpen()
    }
    class Getter(private val declaration: KSPropertyGetter, val calledByNative: KSAnnotation, isStatic: Boolean) : ExposedEntity(isStatic) {

        fun getCallableInfo(context: Context) = CallableInfo(context, declaration)

        val isOpen : Boolean
            get() = declaration.receiver.isOpen()
    }
    class Setter(private val declaration: KSPropertySetter, val calledByNative: KSAnnotation, isStatic: Boolean) : ExposedEntity(isStatic) {

        fun getCallableInfo(context: Context) = CallableInfo(context, declaration)

        val isOpen : Boolean
            get() = declaration.receiver.isOpen()
    }

    class Field(private val declaration: KSPropertyDeclaration, isStatic: Boolean) : ExposedEntity(isStatic) {

        val type: KSType get() = declaration.type.resolve()
        val isMutable : Boolean get() = declaration.isMutable && !declaration.modifiers.contains(Modifier.FINAL)

        val jvmName : String get() = declaration.simpleName.asString()
    }


    companion object {
        fun from(classDeclaration: KSClassDeclaration, context: Context) : JavaClassInfo {
            if (classDeclaration.classKind.let { kind ->
                    kind != ClassKind.INTERFACE && kind != ClassKind.CLASS && kind != ClassKind.ENUM_CLASS && kind != ClassKind.OBJECT
                }) {
                throw ProcessingException("Only named class-like entities can be annotated with ${context.exposedAnnotation}", classDeclaration)
            }

            val javaClassName = context.getJavaNameOfKotlinType(classDeclaration)

            val convertsTo = mutableSetOf<String>()
            collectConvertsTo(classDeclaration, convertsTo, context)

            return JavaClassInfo(javaClassName,
                convertsTo,
                classDeclaration.annotations,
                classDeclaration)
        }

        fun from(file: KSFile, context: Context) : JavaClassInfo? {
            val anyDecl = file.declarations.find {
                it.parentDeclaration == null && it is KSFunctionDeclaration || it is KSPropertyDeclaration
            } ?: return null
            val javaClassName = when (anyDecl) {
                is KSFunctionDeclaration -> context.getOwnerJvmClassName(anyDecl)
                is KSPropertyDeclaration -> context.getOwnerJvmClassName(anyDecl)
                else -> null
            } ?: return null
            return JavaClassInfo(javaClassName,
                emptySet(),
                file.annotations,
                file)
        }

        private fun collectConvertsTo(classDeclaration: KSClassDeclaration,
                                      convertsTo: MutableSet<String>,
                                      context: Context) {

            val superTypes = classDeclaration.superTypes.map { it.resolve() }.toList()

            val superclass = superTypes.filter {
                (it.declaration as? KSClassDeclaration)?.classKind == ClassKind.CLASS
            }.elementAtOrNull(0)?.declaration as? KSClassDeclaration

            if (superclass != null) {
                val superClassName = context.getJavaNameOfKotlinType(superclass)

                if (superClassName != "java.lang.Object") {
                    if (context.typeMap.isMappedJavaType(superClassName))
                        convertsTo.add(superClassName)
                }

                collectConvertsTo(superclass, convertsTo, context)
            }

            superTypes.filter { (it.declaration as? KSClassDeclaration)?.classKind == ClassKind.INTERFACE }.forEach { ifaceType ->

                val superInterface = ifaceType.declaration as KSClassDeclaration
                val superInterfaceName = context.getJavaNameOfKotlinType(superInterface)

                if (context.typeMap.isMappedJavaType(superInterfaceName))
                    convertsTo.add(superInterfaceName)

                collectConvertsTo(superInterface, convertsTo, context)
            }
        }

    }

    fun getShortName() : String {
        return qualifiedJavaClassName.substring((qualifiedJavaClassName.findLastAnyOf(arrayListOf(".", "$"))?.first ?: -1) + 1)
    }

    fun getExposed(context: Context): Sequence<ExposedEntity> {
        return when (node) {
            is KSClassDeclaration -> getExposed(context, node)
            is KSFile -> getExposed(context, node)
            else -> emptySequence()
        }
    }

    private fun getExposed(context: Context, classDeclaration: KSClassDeclaration) : Sequence<ExposedEntity> {

        return sequence {
            for (decl in classDeclaration.declarations) {
                when (decl) {
                    is KSFunctionDeclaration -> yieldAll(handleFunction(context, decl, isFileLevel = false, isFromCompanionObject = false))
                    is KSPropertyDeclaration -> yieldAll(handleProperty(context, decl, isFileLevel = false, isFromCompanionObject = false))
                    is KSClassDeclaration -> if (decl.isCompanionObject) {
                        for (companionDecl in decl.declarations) {
                            when (companionDecl) {
                                is KSFunctionDeclaration -> yieldAll(handleFunction(context, companionDecl, isFileLevel = false, isFromCompanionObject = true))
                                is KSPropertyDeclaration -> yieldAll(handleProperty(context, companionDecl, isFileLevel = false, isFromCompanionObject = true))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getExposed(context: Context, file: KSFile) : Sequence<ExposedEntity> {
        return sequence {
            for (decl in file.declarations) {
                when (decl) {
                    is KSFunctionDeclaration -> yieldAll(handleFunction(context, decl, isFileLevel = true, isFromCompanionObject = false))
                    is KSPropertyDeclaration -> yieldAll(handleProperty(context, decl, isFileLevel = true, isFromCompanionObject = false))
                }
            }
        }
    }

    private fun handleFunction(context: Context, decl: KSFunctionDeclaration,
                               isFileLevel: Boolean,
                               isFromCompanionObject: Boolean) : Sequence<ExposedEntity> {
        val annotations = collectAnnotations(decl.annotations, setOf(context.calledByNativeAnnotation, JvmStatic::class.qualifiedName!!))
        val jvmStatic = annotations.contains(JvmStatic::class.qualifiedName!!)
        if (isFromCompanionObject && !jvmStatic)
            return emptySequence()
        val isParentAnObject = (decl.parentDeclaration as? KSClassDeclaration)?.classKind == ClassKind.OBJECT
        val isStatic = isFileLevel || (jvmStatic && (isParentAnObject || isFromCompanionObject)) ||
                decl.modifiers.contains(Modifier.JAVA_STATIC)
        return if (decl.modifiers.contains(Modifier.EXTERNAL) || decl.modifiers.contains(Modifier.JAVA_NATIVE))
            sequenceOf(NativeFunction(decl, isStatic))
        else {
            val calledByNative = annotations[context.calledByNativeAnnotation] ?: return emptySequence()
            sequenceOf(Function(decl, calledByNative, isStatic))
        }
    }

    private fun handleProperty(context: Context, decl: KSPropertyDeclaration,
                               isFileLevel: Boolean,
                               isFromCompanionObject: Boolean) : Sequence<ExposedEntity> {

        val fieldAnnotations = collectAnnotations(decl.annotations, setOf(context.calledByNativeAnnotation))

        return sequence {
            val isParentAnObject = (decl.parentDeclaration as? KSClassDeclaration)?.classKind == ClassKind.OBJECT

            if (decl.hasBackingField && fieldAnnotations[context.calledByNativeAnnotation] != null) {
                val isStatic = isFileLevel || isFromCompanionObject || isParentAnObject||
                        decl.modifiers.contains(Modifier.JAVA_STATIC)
                yield(Field(decl, isStatic))
            }

            fun <T: KSPropertyAccessor> handleAccessor(accessor: T,
                             maker: (T, calledByNative: KSAnnotation, isStatic: Boolean) -> ExposedEntity) : ExposedEntity? {
                val annotations = collectAnnotations(accessor.annotations,
                    setOf(
                        context.calledByNativeAnnotation,
                        JvmStatic::class.qualifiedName!!
                    )
                )
                val jvmStatic = annotations.contains(JvmStatic::class.qualifiedName!!)
                if (isFromCompanionObject && !jvmStatic)
                    return null
                val isStatic = isFileLevel || (jvmStatic && (isParentAnObject || isFromCompanionObject))
                return annotations[context.calledByNativeAnnotation]?.let { maker(accessor, it, isStatic) }
            }

            decl.getter?.let { getter ->
                handleAccessor(getter) { accessor, calledByNative, isStatic -> Getter(accessor, calledByNative, isStatic) }?.let {
                    yield(it)
                }
            }

            decl.setter?.let { setter ->
                handleAccessor(setter) { accessor, calledByNative, isStatic -> Setter(accessor, calledByNative, isStatic) }?.let {
                    yield(it)
                }
            }
        }
    }

    private fun collectAnnotations(annotations: Sequence<KSAnnotation>, names: Set<String>) : Map<String, KSAnnotation> {
        val ret = mutableMapOf<String, KSAnnotation>()
        for (annotation in annotations) {
            val name = annotation.annotationType.resolve().declaration.qualifiedName?.asString()
            if (name != null && names.contains(name)) {
                ret[name] = annotation
            }
        }
        return ret
    }
}
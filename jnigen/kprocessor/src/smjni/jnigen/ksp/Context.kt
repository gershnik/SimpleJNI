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
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSBuiltIns
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import java.io.File

internal class Context(private val env: SymbolProcessorEnvironment, private val resolver: Resolver) {

    private enum class Options(val externalName: String, val defaultValue: String) {
        DEST_PATH("smjni.jnigen.dest.path", "jnigen"),
        OWN_DEST_PATH("smjni.jnigen.own.dest.path", "false"),
        EXPOSE_EXTRA("smjni.jnigen.expose.extra", ""),
        TYPE_HEADER_NAME("smjni.jnigen.type.header.name", "type_mapping.h"),
        ALL_HEADER_NAME("smjni.jnigen.all.header.name", "all_classes.h"),
        OUTPUT_LIST_NAME("smjni.jnigen.output.list.name", "outputs.txt"),
        EXPOSE_ANNOTATION_NAME("smjni.jnigen.expose.annotation.name", "smjni.jnigen.ExposeToNative"),
        CALLED_ANNOTATION_NAME("smjni.jnigen.called.annotation.name", "smjni.jnigen.CalledByNative"),
        CTOR_NAME("smjni.jnigen.ctor.name", "ctor"),
        PRINT_TO_STDOUT("smjni.jnigen.print.to.stdout", "false");

        fun extract(env: SymbolProcessorEnvironment): String {

            return env.options[externalName] ?: defaultValue
        }
    }

    val headerName = Options.TYPE_HEADER_NAME.extract(env)
    val allHeaderName = Options.ALL_HEADER_NAME.extract(env)
    val outputListName = Options.OUTPUT_LIST_NAME.extract(env)

    val exposedAnnotation = Options.EXPOSE_ANNOTATION_NAME.extract(env)
    val calledByNativeAnnotation = Options.CALLED_ANNOTATION_NAME.extract(env)
    val ctorName = Options.CTOR_NAME.extract(env)

    val exposeExtra: Map<String, String> = run {
        val exposedRegex = Regex("""([^(]+)(?:\(([^)]+)\))?""")
        val exposeMap = mutableMapOf<String, String>()
        for (exposed in Options.EXPOSE_EXTRA.extract(env).split(';').filter { it.trim().isNotEmpty() }) {

            val match = exposedRegex.matchEntire(exposed)
            if (match != null) {
                val javaName = match.groups[1]!!.value
                val stem = match.groups[2]?.value ?: ""
                exposeMap[javaName] = stem
            }
        }
        exposeMap
    }

    val destPath: String = File(Options.DEST_PATH.extract(env)).absolutePath
    val ownDestPath = Options.OWN_DEST_PATH.extract(env) == "true"

    private val printToStdOut = Options.PRINT_TO_STDOUT.extract(env) == "true"

    @KspExperimental
    val typeMap = TypeMap(resolver, env.logger)

    val builtIns: KSBuiltIns get() = resolver.builtIns

    val logger: KSPLogger get() = env.logger

    fun print(message: String, symbol: KSNode? = null) {
        if (printToStdOut)
            println(message)
        else
            logger.info(message, symbol)
    }

    fun getClassDeclarationByName(name: String): KSClassDeclaration? = resolver.getClassDeclarationByName(name)

    @KspExperimental
    fun getJvmName(declaration: KSFunctionDeclaration): String? = resolver.getJvmName(declaration)
    @KspExperimental
    fun getJvmName(accessor: KSPropertyAccessor): String? = resolver.getJvmName(accessor)
    @KspExperimental
    fun getJavaNameOfKotlinType(declaration: KSDeclaration): String = resolver.getJavaNameOfKotlinType(declaration)

    @KspExperimental
    fun getOwnerJvmClassName(declaration: KSPropertyDeclaration): String? = resolver.getOwnerJvmClassName(declaration)

    @KspExperimental
    fun getOwnerJvmClassName(declaration: KSFunctionDeclaration): String? = resolver.getOwnerJvmClassName(declaration)

    fun getExposedSymbols(): Sequence<KSAnnotated> {
        fun checkAnnotation(annotated: KSAnnotated): Boolean {
            return annotated.annotations.any {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == exposedAnnotation
            }
        }

        val visitor = object : KSVisitorVoid() {
            val symbols = mutableSetOf<KSAnnotated>()
            override fun visitAnnotated(annotated: KSAnnotated, data: Unit) {
                if (checkAnnotation(annotated)) {
                    symbols.add(annotated)
                }
            }

            override fun visitFile(file: KSFile, data: Unit) {
                visitAnnotated(file, data)
                file.declarations.forEach { it.accept(this, data) }
            }

            override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
                visitAnnotated(classDeclaration, data)
                classDeclaration.typeParameters.forEach { it.accept(this, data) }
                classDeclaration.declarations.forEach { it.accept(this, data) }
                classDeclaration.primaryConstructor?.accept(this, data)
            }

            override fun visitPropertyGetter(getter: KSPropertyGetter, data: Unit) {
                visitAnnotated(getter, data)
            }

            override fun visitPropertySetter(setter: KSPropertySetter, data: Unit) {
                visitAnnotated(setter, data)
            }

            override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
                visitAnnotated(function, data)
                function.typeParameters.forEach { it.accept(this, data) }
                function.parameters.forEach { it.accept(this, data) }
                function.declarations.forEach { it.accept(this, data) }
            }

            override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
                visitAnnotated(property, data)
                property.typeParameters.forEach { it.accept(this, data) }
                property.getter?.accept(this, data)
                property.setter?.accept(this, data)
            }

            override fun visitTypeParameter(typeParameter: KSTypeParameter, data: Unit) {
                visitAnnotated(typeParameter, data)
                super.visitTypeParameter(typeParameter, data)
            }

            override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit) {
                if (valueParameter.isVal || valueParameter.isVar) {
                    return
                }
                visitAnnotated(valueParameter, data)
            }
        }

        resolver.getAllFiles().forEach { it.accept(visitor, Unit) }
        return visitor.symbols.asSequence()
    }
}
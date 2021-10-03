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
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

@KspExperimental
internal class Processor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {

    private class ExposedData(val cppName: String, val cppClassName: String, val header: String)

    override fun process(resolver: Resolver) : List<KSAnnotated>{

        try {
            val context = Context(env, resolver)

            val cppNames = mutableMapOf<String, CharSequence>()
            val cppClassNames = mutableMapOf<String, CharSequence>()
            val knownClasses = mutableMapOf<JavaClassInfo, ExposedData>()

            context.exposeExtra.forEach { (javaClass, stem) ->
                handleCommandLineSymbol(javaClass, stem, cppNames, cppClassNames, knownClasses, context)
            }

            resolver.getSymbolsWithAnnotation(context.exposedAnnotation).forEach {
                handleAnnotatedSymbol(it, cppNames, cppClassNames, knownClasses, context)
            }

            for ((classInfo, exposedData) in knownClasses) {

                val content = ClassContent(classInfo, exposedData.cppClassName, context)
                context.typeMap.addExposedClass(classInfo, content, exposedData.header)
            }

            Generator(context).generate()
        } catch (ex: ProcessingException) {
            env.logger.error(ex.toString(), ex.declaration)
        }

        return emptyList()
    }

    private fun handleAnnotatedSymbol(symbol: KSAnnotated,
                                      cppNames: MutableMap<String, CharSequence>,
                                      cppClassNames: MutableMap<String, CharSequence>,
                                      knownClasses: MutableMap<JavaClassInfo, ExposedData>,
                                      context: Context) {
        val classInfo = when (symbol) {
            is KSClassDeclaration -> JavaClassInfo.from(symbol, context)
            is KSFile -> JavaClassInfo.from(symbol, context)
            else -> null
        } ?: return

        val exposedData = getExposedDataFromAnnotation(classInfo, cppNames, cppClassNames, context) ?: return

        env.logger.logging("JNIGen: Discovered ${classInfo.qualifiedJavaClassName}")

        knownClasses[classInfo] = exposedData
        cppNames[exposedData.cppName] = classInfo.qualifiedJavaClassName
        cppClassNames[exposedData.cppClassName] = classInfo.qualifiedJavaClassName
        context.typeMap.addJavaToCppMapping(classInfo.qualifiedJavaClassName, exposedData.cppName)
    }

    private fun handleCommandLineSymbol(javaClass: String,
                                        stem: String,
                                        cppNames: MutableMap<String, CharSequence>,
                                        cppClassNames: MutableMap<String, CharSequence>,
                                        knownClasses: MutableMap<JavaClassInfo, ExposedData>,
                                        context: Context) {

        val exposedDeclaration = context.getClassDeclarationByName(javaClass)
        if (exposedDeclaration == null) {
            context.logger.error("Cannot find class $javaClass")
            return
        }

        val classInfo = JavaClassInfo.from(exposedDeclaration, context)

        val exposedData = getExposedDataFromCommandLine(classInfo, stem, cppNames, cppClassNames) ?: return

        env.logger.logging("JNIGen: Discovered (command line) ${classInfo.qualifiedJavaClassName}")

        knownClasses[classInfo] = exposedData
        cppNames[exposedData.cppName] = classInfo.qualifiedJavaClassName
        cppClassNames[exposedData.cppClassName] = classInfo.qualifiedJavaClassName
        context.typeMap.addJavaToCppMapping(classInfo.qualifiedJavaClassName, exposedData.cppName)
    }

    private fun getExposedDataFromAnnotation(classInfo: JavaClassInfo,
                                             cppNames: Map<String, CharSequence>,
                                             cppClassNames: Map<String, CharSequence>,
                                             context: Context) : ExposedData? {

        val annotation = getAnnotation(classInfo, context.exposedAnnotation)
        if (annotation == null) {
            env.logger.error("Annotation ${context.exposedAnnotation} is not configured correctly",
                classInfo.node)
            return null
        }
        val exposedData = getExposedData(annotation, classInfo)
        if (exposedData == null) {
            env.logger.error("Annotation ${context.exposedAnnotation} is not configured correctly",
                classInfo.node)
            return null
        }
        var existing = cppNames[exposedData.cppName]
        if (existing != null) {
            env.logger.error("C++ name ${exposedData.cppName} is already used by $existing",
                             classInfo.node)
            return null
        }

        existing = cppClassNames[exposedData.cppClassName]
        if (existing != null) {
            env.logger.error("C++ name ${exposedData.cppClassName} is already used by $existing",
                             classInfo.node)
            return null
        }
        return exposedData
    }

    private fun getExposedDataFromCommandLine(classInfo: JavaClassInfo,
                                              stem: String,
                                              cppNames: Map<String, CharSequence>,
                                              cppClassNames: Map<String, CharSequence>) : ExposedData? {

        val exposedData = makeExposedData(classInfo, stem)

        var existing = cppNames[exposedData.cppName]
        if (existing != null) {
            env.logger.error("C++ name ${exposedData.cppName} is already used by $existing",
                    classInfo.node)
            return null
        }

        existing = cppClassNames[exposedData.cppClassName]
        if (existing != null) {
            env.logger.error("C++ name ${exposedData.cppClassName} is already used by $existing",
                classInfo.node)
            return null
        }
        return exposedData
    }

    private fun getAnnotation(classInfo: JavaClassInfo, name: String): KSAnnotation? {

        for(annotation in classInfo.annotations) {
            val annotationType = annotation.annotationType.resolve()
            if (annotationType.declaration.qualifiedName?.asString() == name)
                return annotation
        }
        return null
    }

    private fun getExposedData(annotation: KSAnnotation, classInfo: JavaClassInfo) : ExposedData? {

        var stem: String? = null
        var cppName: String? = null
        var cppClassName: String? = null
        var header: String? = null
        for(arg in annotation.arguments) {

            when {
                arg.name?.getShortName() == "value" -> stem = arg.value?.toString() ?: ""
                arg.name?.getShortName() == "typeName" -> cppName = arg.value?.toString()
                arg.name?.getShortName() == "className" -> cppClassName = arg.value?.toString()
                arg.name?.getShortName() == "header" -> header = arg.value?.toString()
            }
        }
        if (stem == null)
            return null

        return makeExposedData(classInfo, stem, cppName, cppClassName, header)

    }

    private fun makeExposedData(classInfo: JavaClassInfo,
                                stem: String,
                                cppName: String? = null,
                                cppClassName: String? = null,
                                header: String? = null): ExposedData
    {

        val derivedStem = stem.ifEmpty { classInfo.getShortName() }

        val derivedCppName = if (cppName == null || cppName.isEmpty())
            "j$derivedStem"
        else
            cppName

        val derivedCppClassName = if (cppClassName == null || cppClassName.isEmpty())
            "${derivedStem}_class"
        else
            cppClassName

        val derivedHeader = if (header == null || header.isEmpty())
            "$derivedCppClassName.h"
        else
            header

        return ExposedData(derivedCppName, derivedCppClassName, derivedHeader)
    }


}
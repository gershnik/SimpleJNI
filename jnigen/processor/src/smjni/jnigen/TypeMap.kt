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

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

internal class TypeMap(ctxt: Context, env: RoundEnvironment) {

    private val _exposedClasses = HashMap<TypeElement, ClassContent>()
    private val _javaToCppNameMap = HashMap<String, String>()
    private val _exposedArrays = HashSet<String>()
    private val _byFile = HashMap<String, MutableList<TypeElement>>()

    private val exposedToNativeAnnotationName = ctxt.exposedAnnotation

    private class ExposedData(val cppName: String, val cppClassName: String, val header: String)

    init {

        _javaToCppNameMap["java.lang.Object"] = "jobject"
        _javaToCppNameMap["java.lang.String"] = "jstring"
        _javaToCppNameMap["java.lang.Throwable"] = "jthrowable"
        _javaToCppNameMap["java.lang.Class"] = "jclass"
        _javaToCppNameMap["java.nio.ByteBuffer"] = "jByteBuffer"

        val exposedToNative = ctxt.elementUtils.getTypeElement(exposedToNativeAnnotationName)

        val cppNames = HashMap<String, CharSequence>()
        val cppClassNames = HashMap<String, CharSequence>()
        val knownClasses = HashMap<TypeElement, ExposedData>()

        for ((javaClass, stem) in ctxt.exposeExtra) {

            val exposedElement = ctxt.elementUtils.getTypeElement(javaClass)
            if (exposedElement == null) {
                ctxt.messager.printMessage(Diagnostic.Kind.ERROR, "Cannot find class $javaClass")
                continue
            }
            val kind = exposedElement.kind

            if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE && kind != ElementKind.ENUM) {
                ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Only classes can be annotated with $exposedToNativeAnnotationName",
                        exposedElement)
                continue
            }
            val exposedData = getExposedDataFromCommandLine(exposedElement, stem, cppNames, cppClassNames, ctxt) ?: continue

            ctxt.print("JNIGen: Discovered (command line) ${exposedElement.qualifiedName}")

            knownClasses[exposedElement] = exposedData
            cppNames[exposedData.cppName] = exposedElement.qualifiedName
            cppClassNames[exposedData.cppClassName] = exposedElement.qualifiedName
            _javaToCppNameMap[exposedElement.qualifiedName.toString()] = exposedData.cppName
        }

        for (annotatedElement in env.getElementsAnnotatedWith(exposedToNative)){

            val kind = annotatedElement.kind

            if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE && kind != ElementKind.ENUM) {
                ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                                     "Only classes can be annotated with $exposedToNativeAnnotationName",
                                           annotatedElement)
                continue
            }

            val classElement = annotatedElement as TypeElement
            val exposedData = getExposedDataFromAnnotation(classElement, cppNames, cppClassNames, ctxt) ?: continue

            ctxt.print("JNIGen: Discovered ${classElement.qualifiedName}")

            knownClasses[classElement] = exposedData
            cppNames[exposedData.cppName] = classElement.qualifiedName
            cppClassNames[exposedData.cppClassName] = classElement.qualifiedName
            _javaToCppNameMap[classElement.qualifiedName.toString()] = exposedData.cppName

            val byHeaderList = _byFile[exposedData.header] ?: ArrayList()
            byHeaderList.add(classElement)
            _byFile[exposedData.header] = byHeaderList
        }

        for ((classElement, exposedData) in knownClasses) {

            val convertsTo = HashSet<String>()
            collectConvertsTo(classElement, knownClasses, convertsTo)
            val binaryName = ctxt.elementUtils.getBinaryName(classElement).toString()
            val content = ClassContent(classElement, binaryName, exposedData.cppClassName, convertsTo, this, ctxt)
            _exposedClasses[classElement] = content
        }
    }

    internal val exposedClasses: Map<TypeElement, ClassContent>
        get() = _exposedClasses

    internal val exposedArrays: Set<String>
        get() = _exposedArrays

    internal val classHeaders: Collection<String>
        get() = _byFile.keys

    internal fun classesInHeader(header: String) : Sequence<ClassContent> {

        return (_byFile[header] ?: emptyList()).asSequence().map { _exposedClasses[it]!! }
    }

    internal fun nativeNameOf(type: TypeMirror): String {

        return when (type.kind) {

            TypeKind.BOOLEAN    -> "jboolean"
            TypeKind.BYTE       -> "jbyte"
            TypeKind.CHAR       -> "jchar"
            TypeKind.SHORT      -> "jshort"
            TypeKind.INT        -> "jint"
            TypeKind.LONG       -> "jlong"
            TypeKind.FLOAT      -> "jfloat"
            TypeKind.DOUBLE     -> "jdouble"
            TypeKind.VOID       -> "void"
            TypeKind.DECLARED -> {
                val element = ((type as DeclaredType).asElement() as TypeElement)
                nativeNameOf(element)
            }
            TypeKind.TYPEVAR -> {
                val element = ((type as TypeVariable).asElement() as TypeParameterElement)
                nativeNameOf(element)
            }
            TypeKind.ARRAY -> {

                val array = type as ArrayType
                val itemType = array.componentType
                val itemTypeName = nativeNameOf(itemType)
                val arrayTypeName = "${itemTypeName}Array"
                if (!itemType.kind.isPrimitive && itemTypeName != "jobject") {
                    _exposedArrays.add(itemTypeName)
                }
                arrayTypeName
            }
            else -> {
                error("Logic error: impossible type kind: ${type.kind.name}")
            }
        }
    }

    internal fun nativeNameOf(javaName: CharSequence): String? {

        return _javaToCppNameMap[javaName.toString()]
    }

    internal fun wrapperNameOf(type: TypeMirror, isArgument: Boolean) : String {
        return when (val rawType = nativeNameOf(type)) {

            "jboolean"    -> "jboolean"
            "jbyte"       -> "jbyte"
            "jchar"       -> "jchar"
            "jshort"      -> "jshort"
            "jint"        -> "jint"
            "jlong"       -> "jlong"
            "jfloat"      -> "jfloat"
            "jdouble"     -> "jdouble"
            "void"        -> "void"
            else -> {
                if (isArgument)
                    "const smjni::auto_java_ref<$rawType> &"
                else
                    "smjni::local_java_ref<$rawType>"
            }
        }
    }

    private fun nativeNameOf(el: TypeElement): String {

        val ret = _javaToCppNameMap[el.qualifiedName.toString()]
            ?: throw ProcessingException("${el.qualifiedName} is not exposed to C++ via annotation or command line", el)
        return ret
    }

    private fun nativeNameOf(el: TypeParameterElement): String {
        val bounds = el.bounds
        if (bounds.size > 1)
            return "jobject"
        return nativeNameOf(bounds[0])
    }

    private fun getExposedDataFromAnnotation(classElement: TypeElement,
                                             cppNames: Map<String, CharSequence>,
                                             cppClassNames: Map<String, CharSequence>,
                                             ctxt: Context) : ExposedData? {

        val annotation = getAnnotation(classElement, exposedToNativeAnnotationName)
        if (annotation == null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "Annotation $exposedToNativeAnnotationName is not configured correctly",
                    classElement)
            return null
        }
        val exposedData = getExposedData(annotation, classElement, ctxt.elementUtils)
        if (exposedData == null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "Annotation $exposedToNativeAnnotationName is not configured correctly",
                    classElement,
                    annotation)
            return null
        }

        var existing = cppNames[exposedData.cppName]
        if (existing != null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "C++ name ${exposedData.cppName} is already used by $existing",
                    classElement,
                    annotation)
            return null
        }

        existing = cppClassNames[exposedData.cppClassName]
        if (existing != null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "C++ name ${exposedData.cppClassName} is already used by $existing",
                    classElement,
                    annotation)
            return null
        }
        return exposedData
    }

    private fun getExposedDataFromCommandLine(classElement: TypeElement,
                                              stem: String,
                                              cppNames: Map<String, CharSequence>,
                                              cppClassNames: Map<String, CharSequence>,
                                              ctxt: Context) : ExposedData? {

        val exposedData = makeExposedData(classElement, stem)

        var existing = cppNames[exposedData.cppName]
        if (existing != null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "C++ name ${exposedData.cppName} is already used by $existing",
                    classElement)
            return null
        }

        existing = cppClassNames[exposedData.cppClassName]
        if (existing != null) {
            ctxt.messager.printMessage(Diagnostic.Kind.ERROR,
                    "C++ name ${exposedData.cppClassName} is already used by $existing",
                    classElement)
            return null
        }
        return exposedData

    }

    private fun collectConvertsTo(classElement: TypeElement,
                                  knownClasses: Map<TypeElement, ExposedData>,
                                  convertsTo: MutableSet<String>) {

        if (classElement.superclass.kind != TypeKind.NONE) {
            val superClass = (classElement.superclass as DeclaredType).asElement() as TypeElement
            val superClassName = superClass.qualifiedName.toString()

            if (!superClassName.contentEquals("java.lang.Object")) {
                if (_javaToCppNameMap.containsKey(superClassName))
                    convertsTo.add(superClassName)
            }

            collectConvertsTo(superClass, knownClasses, convertsTo)
        }

        for (item in classElement.interfaces) {

            val superInterface = (item as DeclaredType).asElement() as TypeElement
            val superInterfaceName = superInterface.qualifiedName.toString()

            if (_javaToCppNameMap.containsKey(superInterfaceName))
                convertsTo.add(superInterfaceName)

            collectConvertsTo(superInterface, knownClasses, convertsTo)
        }
    }

    private fun getAnnotation(classElement: TypeElement, name: String): AnnotationMirror? {

        for(annotationMirror in classElement.annotationMirrors) {

            val annotationType = annotationMirror.annotationType.asElement() as TypeElement
            if (annotationType.qualifiedName.contentEquals(name))
                return annotationMirror
        }
        return null
    }

    private fun getExposedData(annotation: AnnotationMirror, classElement: TypeElement, elements: Elements) : ExposedData? {

        var stem: String? = null
        var cppName: String? = null
        var cppClassName: String? = null
        var header: String? = null
        for((name, value) in elements.getElementValuesWithDefaults(annotation)) {

            when {
                name.simpleName.contentEquals("value") -> stem = value.value.toString()
                name.simpleName.contentEquals("typeName") -> cppName = value.value.toString()
                name.simpleName.contentEquals("className") -> cppClassName = value.value.toString()
                name.simpleName.contentEquals("header") -> header = value.value.toString()
            }
        }
        if (stem == null)
            return null

        return makeExposedData(classElement, stem, cppName, cppClassName, header)

    }

    private fun makeExposedData(classElement: TypeElement, stem: String,
                                cppName: String? = null,
                                cppClassName: String? = null,
                                header: String? = null): ExposedData
    {

        val derivedStem = stem.ifEmpty { getStemName(classElement) }

        val derivedCppName = if (cppName.isNullOrEmpty())
            "j$derivedStem"
        else
            cppName

        val derivedCppClassName = if (cppClassName.isNullOrEmpty())
            "${derivedStem}_class"
        else
            cppClassName

        val derivedHeader = if (header.isNullOrEmpty())
            "$derivedCppClassName.h"
        else
            header

        return ExposedData(derivedCppName, derivedCppClassName, derivedHeader)
    }

    private fun getStemName(classElement: TypeElement) : String {
        var packageElement = classElement.enclosingElement
        while (true) {
            if (packageElement.kind == ElementKind.PACKAGE)
                break
            packageElement = packageElement.enclosingElement
        }
        val packageName = (packageElement as PackageElement).qualifiedName

        return if (packageName.isNotEmpty()) {
            classElement.qualifiedName.toString().removePrefix("${packageElement.qualifiedName}.")
                    .replace(Regex("""\."""), "_")
        } else {
            classElement.simpleName.toString()
        }
    }


}
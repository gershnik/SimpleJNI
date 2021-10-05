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

import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*


internal class Generator {

    internal fun generate(typeMap: TypeMap, context: Context) {

        generateTypeHeader(typeMap, context)

        val allHeaders = ArrayList<String>()
        for (header in typeMap.classHeaders) {
            if (generateClassHeader(header, typeMap, context))
                allHeaders.add(header)
        }

        generateAllClassesHeader(allHeaders, typeMap, context)
        generateOutputsList(allHeaders, context)
    }

    private fun generateTypeHeader(typeMap: TypeMap, context: Context) {

        val generated = generateFile("${context.destPath}/${context.headerName}") { typeHeader ->

            val guardName = "HEADER_${makeGuardName(context.headerName)}_INCLUDED"
            typeHeader.write("#ifndef $guardName\n")
            typeHeader.write("#define $guardName\n\n\n")
            typeHeader.write("//THIS FILE IS AUTO-GENERATED. DO NOT EDIT\n\n")
            typeHeader.write("#include <smjni/smjni.h>\n\n")

            val exposedClasses = ArrayList(typeMap.exposedClasses.values)
            exposedClasses.sortBy { it.cppName }

            for (classContent in exposedClasses) {
                typeHeader.write("DEFINE_JAVA_TYPE(${classContent.cppName},  \"${classContent.binaryName}\")\n")
            }
            typeHeader.write("\n")

            for (arrayType in typeMap.exposedArrays.toTypedArray().sorted())
                typeHeader.write("DEFINE_ARRAY_JAVA_TYPE($arrayType)\n")
            typeHeader.write("\n")

            for (classContent in exposedClasses)
                for (item in classContent.convertsTo.sorted())
                    typeHeader.write("DEFINE_JAVA_CONVERSION(${typeMap.nativeNameOf(item)}, ${classContent.cppName})\n")

            typeHeader.write("\n#endif\n")
        }

        context.print("JNIGen: Generating ${context.headerName}:" +
            if (generated)
                "  written"
            else
                "  up-to-date"
        )
    }

    private fun generateClassHeader(header: String, typeMap: TypeMap, context: Context) : Boolean {

        if (typeMap.classesInHeader(header).find{ it.javaEntities.isNotEmpty() || it.nativeMethods.isNotEmpty() } == null)
            return false

        val generated = generateFile("${context.destPath}/$header") { classHeader ->

            val guardName = "HEADER_${makeGuardName(header)}_INCLUDED"
            classHeader.write("#ifndef $guardName\n")
            classHeader.write("#define $guardName\n\n\n")
            classHeader.write("//THIS FILE IS AUTO-GENERATED. DO NOT EDIT\n\n")
            classHeader.write("#include \"${context.headerName}\"\n\n")

            typeMap.classesInHeader(header).forEach { classContent ->
                if (classContent.javaEntities.isNotEmpty() || classContent.nativeMethods.isNotEmpty())
                    generateClassDef(classHeader, classContent)
            }

            classHeader.write("#endif\n")
        }

        context.print("JNIGen: Generating $header:" +
            if (generated)
                "  written"
            else
                "  up-to-date"
        )

        return true
    }

    private fun generateClassDef(classHeader: FileWriter, content: ClassContent)  {

        classHeader.write("class ${content.cppClassName} : public smjni::java_runtime::simple_java_class<${content.cppName}>\n" +
                "{\n" +
                "public:\n" +
                "    ${content.cppClassName}(JNIEnv * env);\n\n")

        if (content.nativeMethods.isNotEmpty())
            classHeader.write("    void register_methods(JNIEnv * env) const;\n\n")

        generateJavaEntityAccessors(content.javaEntities, classHeader)

        classHeader.write("private:\n")
        generateNativeMethodDeclarations(content.nativeMethods, classHeader)
        generateJavaEntityFields(content.javaEntities, classHeader)

        classHeader.write("};\n\n\n")

        generateConstructorImplementation(content, classHeader)

        generateRegistrationMethodImplementation(content, classHeader)
    }

    private fun generateJavaEntityAccessors(javaEntities: List<JavaEntity>, classHeader: FileWriter) {

        for(javaEntity in javaEntities) {

            val argNameTable = NameTable()
            val argNames = ArrayList<UniqueName>()
            argNames.add(argNameTable.allocateName("env"))
            if (javaEntity.allowNonVirt)
                argNameTable.allocateName("classForNonVirtualCall")
            javaEntity.argNames.mapTo(argNames) { argNameTable.allocateName(it) }

            when(javaEntity.type) {
                JavaEntityType.Method, JavaEntityType.StaticMethod, JavaEntityType.Constructor -> {
                    classHeader.write("    ${javaEntity.returnType} ${javaEntity.name}(JNIEnv * env")
                    for (i in 0 until javaEntity.argTypes.size) {
                        classHeader.write(", ${javaEntity.argTypes[i]} ${argNames[i + 1]}")
                    }
                    val memberName = "m_${javaEntity.name}"
                    classHeader.write(") const\n        { ")
                    if (javaEntity.returnType != "void")
                        classHeader.write("return ")
                    classHeader.write("$memberName(env")
                    if (javaEntity.type == JavaEntityType.StaticMethod ||  javaEntity.type == JavaEntityType.Constructor)
                        classHeader.write(", *this")
                    for (i in 0 until javaEntity.argTypes.size) {
                        classHeader.write(", ${argNames[i + 1]}")
                    }
                    classHeader.write("); }\n")

                    if (javaEntity.allowNonVirt) {
                        classHeader.write("    template<typename ClassType> ${javaEntity.returnType} ${javaEntity.name}(JNIEnv * env")
                        classHeader.write(", ${javaEntity.argTypes[0]} ${argNames[1]}")
                        classHeader.write(", const java_class<ClassType> & classForNonVirtualCall")
                        for (i in 1 until javaEntity.argTypes.size) {
                            classHeader.write(", ${javaEntity.argTypes[i]} ${argNames[i + 1]}")
                        }
                        classHeader.write(") const\n        { ")
                        if (javaEntity.returnType != "void")
                            classHeader.write("return ")
                        classHeader.write("$memberName.call_non_virtual(env")
                        classHeader.write(", ${argNames[1]}, classForNonVirtualCall")
                        for (i in 1 until javaEntity.argTypes.size) {
                            classHeader.write(", ${argNames[i + 1]}")
                        }
                        classHeader.write("); }\n")
                    }
                }
                JavaEntityType.Field, JavaEntityType.StaticField -> {

                    val memberName = "m_${javaEntity.name}"

                    val getter = "get_${javaEntity.name}"
                    classHeader.write("    ${javaEntity.returnType} $getter(JNIEnv * env")
                    if (javaEntity.argTypes.size == 2) {
                        classHeader.write(", ${javaEntity.argTypes[0]} ${argNames[1]}")
                    }
                    classHeader.write(") const\n        { return $memberName.get(env")
                    if (javaEntity.type == JavaEntityType.StaticField)
                        classHeader.write(", *this")
                    if (javaEntity.argTypes.size == 2) {
                        classHeader.write(", ${argNames[1]}")
                    }
                    classHeader.write("); }\n")

                    if (!javaEntity.isFinal) {
                        val setter = "set_${javaEntity.name}"
                        classHeader.write("    void $setter(JNIEnv * env")
                        if (javaEntity.argTypes.size == 2) {
                            classHeader.write(", ${javaEntity.argTypes[0]} ${argNames[1]}")
                            classHeader.write(", ${javaEntity.argTypes[1]} value")
                        } else {
                            classHeader.write(", ${javaEntity.argTypes[0]} value")
                        }
                        classHeader.write(") const\n        { $memberName.set(env")
                        if (javaEntity.type == JavaEntityType.StaticField)
                            classHeader.write(", *this")
                        if (javaEntity.argTypes.size == 2) {
                            classHeader.write(", ${argNames[1]}")
                        }
                        classHeader.write(", value); }\n")
                    }
                }
            }

        }
    }

    private fun generateJavaEntityFields(javaEntities: List<JavaEntity>, classHeader: FileWriter) {

        if (javaEntities.isNotEmpty()) {
            for (javaEntity in javaEntities) {

                when (javaEntity.type) {
                    JavaEntityType.Method -> classHeader.write("    const smjni::java_method<")
                    JavaEntityType.StaticMethod -> classHeader.write("    const smjni::java_static_method<")
                    JavaEntityType.Field -> classHeader.write("    const smjni::java_field<")
                    JavaEntityType.StaticField -> classHeader.write("    const smjni::java_static_field<")
                    JavaEntityType.Constructor -> classHeader.write("    const smjni::java_constructor<")
                }
                classHeader.write(javaEntity.templateArguments.joinToString(separator = ", "))
                val memberName = "m_${javaEntity.name}"
                classHeader.write("> $memberName;\n")
            }
            classHeader.write("\n")
        }
    }

    private fun generateNativeMethodDeclarations(nativeMethods: List<NativeMethod>, classHeader: FileWriter) {

        if (nativeMethods.isNotEmpty()) {
            for (nativeMethod in nativeMethods) {

                classHeader.write("    static ${nativeMethod.returnType} JNICALL ")
                classHeader.write("${nativeMethod.name}(")

                val argNameTable = NameTable()
                val args = nativeMethod.arguments.joinToString(separator = ", ", transform = { arg ->

                    when(val argName = arg.second) {
                        null -> arg.first
                        else -> "${arg.first} ${argNameTable.allocateName(argName)}"
                    }
                })
                classHeader.write("$args);\n")
            }
            classHeader.write("\n")
        }
    }

    private fun generateRegistrationMethodImplementation(content: ClassContent, classHeader: FileWriter) {

        if (content.nativeMethods.isNotEmpty()) {

            classHeader.write("inline void ${content.cppClassName}::register_methods(JNIEnv * env) const\n")
            classHeader.write("{\n")
            classHeader.write("    register_natives(env, {\n")
            for(nativeMethod in content.nativeMethods) {

                val cppName = StringBuilder()
                if (nativeMethod.isNameNonUnique) {

                    cppName.append("(${nativeMethod.returnType} (JNICALL *)(")
                    nativeMethod.arguments.joinTo(buffer = cppName, separator = ", ", transform = { arg ->
                        arg.first
                    })
                    cppName.append("))")
                }
                cppName.append(nativeMethod.name)

                if (nativeMethod.isStatic) {
                    classHeader.write("        bind_native(\"${nativeMethod.name}\", $cppName),\n")
                }
                else {
                    classHeader.write("        bind_native(\"${nativeMethod.name}\", $cppName),\n")
                }
            }
            classHeader.write("    });\n")
            classHeader.write("}\n\n")
        }
    }

    private fun generateConstructorImplementation(content: ClassContent, classHeader: FileWriter) {

        classHeader.write("inline ${content.cppClassName}::${content.cppClassName}(JNIEnv * env):\n")
        classHeader.write("    simple_java_class(env)")

        for (javaEntity in content.javaEntities) {
            val memberName = "m_${javaEntity.name}"

            when (javaEntity.type) {
                JavaEntityType.Constructor ->
                    classHeader.write(",\n    $memberName(env, *this)")
                else ->
                    classHeader.write(",\n    $memberName(env, *this, \"${javaEntity.name.original}\")")
            }
        }
        classHeader.write("\n{}\n\n")
    }

    private fun generateAllClassesHeader(headers: List<String>, typeMap: TypeMap, context: Context) {

        val generated = generateFile("${context.destPath}/${context.allHeaderName}") { allHeader ->

            val guardName = "HEADER_${makeGuardName(context.allHeaderName)}_INCLUDED"
            allHeader.write("#ifndef $guardName\n")
            allHeader.write("#define $guardName\n\n\n")
            allHeader.write("//THIS FILE IS AUTO-GENERATED. DO NOT EDIT\n\n")

            for (header in headers)
                allHeader.write("#include \"$header\"\n")

            allHeader.write("\n#define JNIGEN_ALL_GENERATED_CLASSES \\\n    ")

            allHeader.write(headers.joinToString(separator = ", \\\n    ") { header -> typeMap.classesInHeader(header).filter {it.hasCppClass }.map { it.cppClassName }.joinToString(separator = ", \\\n    ")})

            allHeader.write("\n\n#endif\n")
        }

        context.print("JNIGen: Generating ${context.allHeaderName}:" +
            if (generated)
                "  written"
            else
                "  up-to-date"
        )
    }

    private fun generateOutputsList(headers: List<String>, context: Context) {

        val generated = generateFile("${context.destPath}/${context.outputListName}") { outList ->

            outList.write("${context.headerName}\n")
            outList.write("${context.allHeaderName}\n")
            for (header in headers)
                outList.write("$header\n")
        }

        context.print("JNIGen: Generating ${context.outputListName}:" +
            if (generated)
                "  written"
            else
                "  up-to-date"
        )
    }

    private fun generateFile(name: String, generator: (FileWriter) -> Unit): Boolean {

        val existing = File(name)
        val dir = existing.parentFile
        Files.createDirectories(Paths.get(dir.absolutePath))
        val temp = File.createTempFile("jnigen", null, dir)
        temp.deleteOnExit()
        try {

            FileWriter(temp).use {
                generator(it)
            }

            val existingDigest = digestFile(name)
            if (existingDigest != null) {
                val newDigest = digestFile(temp.absolutePath)
                if (newDigest!!.contentEquals(existingDigest)) {
                    return false
                }
                Files.delete(Paths.get(existing.absolutePath))
            }

            Files.move(Paths.get(temp.absolutePath), Paths.get(existing.absolutePath),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)

            return true

        } finally {

            temp.delete()
        }
    }

    private fun digestFile(path: String) : ByteArray? {

        val md = MessageDigest.getInstance("MD5")
        try {
            Files.newInputStream(Paths.get(path)).use { str ->
                DigestInputStream(str, md).use { dstr ->
                    val buffer = ByteArray(1024)
                    while (dstr.read(buffer) != -1) { /* empty body*/ }
                }
            }
        } catch (ex: java.nio.file.NoSuchFileException) {
            return null
        }

        return md.digest()
    }

    private fun makeGuardName(fileName: String): String {

        return fileName.replace(Regex("""[-.!@#$%^&*()+\\{}\[\];"?<>,~`]"""), "_").uppercase(Locale.getDefault())
    }
}
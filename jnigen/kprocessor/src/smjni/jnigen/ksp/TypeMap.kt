/*
 Copyright 2021 SimpleJNI Contributors

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
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference

@KspExperimental
internal class TypeMap(private val resolver: Resolver, private val logger: KSPLogger) {

    private val _javaToCppNameMap = mutableMapOf(
        Pair("java.lang.Object",    "jobject"),
        Pair("java.lang.String",    "jstring"),
        Pair("java.lang.Throwable", "jthrowable"),
        Pair("java.lang.Class",     "jclass"),
        Pair("java.nio.ByteBuffer", "jByteBuffer"),

        Pair("kotlin.BooleanArray", "jbooleanArray"),
        Pair("kotlin.ByteArray",    "jbyteArray"),
        Pair("kotlin.CharArray",    "jcharArray"),
        Pair("kotlin.ShortArray",   "jshortArray"),
        Pair("kotlin.IntArray",     "jintArray"),
        Pair("kotlin.LongArray",    "jlongArray"),
        Pair("kotlin.FloatArray",   "jfloatArray"),
        Pair("kotlin.DoubleArray",  "jdoubleArray")
    )

    private val _exposedClasses = mutableMapOf<JavaClassInfo, ClassContent>()
    val exposedClasses: Map<JavaClassInfo, ClassContent>
        get() = _exposedClasses

    private val _exposedArrays = mutableSetOf<String>()
    val exposedArrays: Set<String>
        get() = _exposedArrays

    private val _byFile = mutableMapOf<String, MutableList<JavaClassInfo>>()
    val classHeaders: Collection<String>
        get() = _byFile.keys

    private val reportedUnmapped = mutableSetOf<String>()

    fun addJavaToCppMapping(qualifiedName: String, cppName: String) {
        _javaToCppNameMap[qualifiedName] = cppName
    }

    fun addExposedClass(classInfo: JavaClassInfo, content: ClassContent, header: String) {
        _exposedClasses[classInfo] = content
        _byFile.computeIfAbsent(header) { mutableListOf() }.add(classInfo)
    }

    fun isMappedJavaType(qualifiedName: String) = _javaToCppNameMap.containsKey(qualifiedName)

    fun classesInHeader(header: String) : Sequence<ClassContent> {
        return (_byFile[header] ?: emptyList()).asSequence().map { _exposedClasses[it]!! }
    }

    fun nativeNameOf(javaName: String): String {

        return _javaToCppNameMap[javaName] ?: throw Exception("Name $javaName is not mapped")
    }

    fun nativeNameOf(type: KSType): String {

        val starProjection = type.starProjection()
        return when (starProjection) {
            resolver.builtIns.booleanType    -> "jboolean"
            resolver.builtIns.byteType       -> "jbyte"
            resolver.builtIns.charType       -> "jchar"
            resolver.builtIns.shortType      -> "jshort"
            resolver.builtIns.intType        -> "jint"
            resolver.builtIns.longType       -> "jlong"
            resolver.builtIns.floatType      -> "jfloat"
            resolver.builtIns.doubleType     -> "jdouble"
            resolver.builtIns.nothingType    -> "void"
            resolver.builtIns.unitType       -> "void"
            resolver.builtIns.anyType        -> "jobject"
            resolver.builtIns.arrayType -> {

                val itemTypeReference = type.arguments.first().type!!
                val itemTypeName = nativeNameOf(itemTypeReference.resolve())
                val arrayTypeName = "${itemTypeName}Array"
                if (!isPrimitive(itemTypeReference) && itemTypeName != "jobject") {
                    _exposedArrays.add(itemTypeName)
                }
                arrayTypeName
            }
            else -> {
                val javaName = resolver.getJavaNameOfKotlinType(starProjection.declaration)
                _javaToCppNameMap.computeIfAbsent(javaName) {
                    val typeName = (type.declaration.qualifiedName ?: type.declaration.simpleName).asString()
                    if (!reportedUnmapped.contains(typeName)) {
                        logger.logging("Type ${typeName} is not exposed to SimpleJNI and will be represented as jobject")
                        reportedUnmapped.add(typeName)
                    }
                    "jobject"
                }
            }
        }
    }

    fun wrapperNameOf(type: KSType, isArgument: Boolean) : String {
        return wrapperNameOf(nativeNameOf(type), isArgument)
    }

    fun wrapperNameOf(cppType: String, isArgument: Boolean) : String {
        return when (cppType) {

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
                    "const smjni::auto_java_ref<$cppType> &"
                else
                    "smjni::local_java_ref<$cppType>"
            }
        }
    }

    private fun isPrimitive(typeReference: KSTypeReference) : Boolean {
        return when (typeReference.resolve()) {
            resolver.builtIns.booleanType -> true
            resolver.builtIns.byteType -> true
            resolver.builtIns.charType -> true
            resolver.builtIns.shortType -> true
            resolver.builtIns.intType -> true
            resolver.builtIns.longType -> true
            resolver.builtIns.floatType -> true
            resolver.builtIns.doubleType -> true
            else -> false
        }
    }
}
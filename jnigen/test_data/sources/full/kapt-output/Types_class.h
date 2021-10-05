#ifndef HEADER_TYPES_CLASS_H_INCLUDED
#define HEADER_TYPES_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class Types_class : public smjni::java_runtime::simple_java_class<jTypes>
{
public:
    Types_class(JNIEnv * env);

    void primitives(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, jbyte p0_1519748, jchar p1_1526187, jboolean b_ool, jshort p3_54706750, jint p4_52215, jlong p5_1663806, jfloat p6_48763182, jdouble p7_1484504552) const
        { m_primitives(env, self, p0_1519748, p1_1526187, b_ool, p3_54706750, p4_52215, p5_1663806, p6_48763182, p7_1484504552); }
    void nullablePrimitives(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jByte> & p0_1519748, const smjni::auto_java_ref<jChar> & p1_1526187, const smjni::auto_java_ref<jBool> & b_ool, const smjni::auto_java_ref<jShort> & p3_54706750, const smjni::auto_java_ref<jInteger> & p4_52215, const smjni::auto_java_ref<jLong> & p5_1663806, const smjni::auto_java_ref<jFloat> & p6_48763182, const smjni::auto_java_ref<jDouble> & p7_1484504552) const
        { m_nullablePrimitives(env, self, p0_1519748, p1_1526187, b_ool, p3_54706750, p4_52215, p5_1663806, p6_48763182, p7_1484504552); }
    void objects(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jobject> & obj, const smjni::auto_java_ref<jstring> & string, const smjni::auto_java_ref<jthrowable> & throwable, const smjni::auto_java_ref<jclass> & cls, const smjni::auto_java_ref<jByteBuffer> & byteBuffer) const
        { m_objects(env, self, obj, string, throwable, cls, byteBuffer); }
    void primitiveArrays(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jbyteArray> & byteArray, const smjni::auto_java_ref<jcharArray> & charArray, const smjni::auto_java_ref<jbooleanArray> & boolArray, const smjni::auto_java_ref<jshortArray> & shortArray, const smjni::auto_java_ref<jintArray> & intArray, const smjni::auto_java_ref<jlongArray> & longArray, const smjni::auto_java_ref<jfloatArray> & floatArray, const smjni::auto_java_ref<jdoubleArray> & doubleArray) const
        { m_primitiveArrays(env, self, byteArray, charArray, boolArray, shortArray, intArray, longArray, floatArray, doubleArray); }
    void objectArrays(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jobjectArray> & objArray, const smjni::auto_java_ref<jstringArray> & stringArray, const smjni::auto_java_ref<jthrowableArray> & throwableArray, const smjni::auto_java_ref<jclassArray> & clsArray, const smjni::auto_java_ref<jByteBufferArray> & byteBufferArray) const
        { m_objectArrays(env, self, objArray, stringArray, throwableArray, clsArray, byteBufferArray); }
private:
    const smjni::java_method<void, jTypes, jbyte, jchar, jboolean, jshort, jint, jlong, jfloat, jdouble> m_primitives;
    const smjni::java_method<void, jTypes, jByte, jChar, jBool, jShort, jInteger, jLong, jFloat, jDouble> m_nullablePrimitives;
    const smjni::java_method<void, jTypes, jobject, jstring, jthrowable, jclass, jByteBuffer> m_objects;
    const smjni::java_method<void, jTypes, jbyteArray, jcharArray, jbooleanArray, jshortArray, jintArray, jlongArray, jfloatArray, jdoubleArray> m_primitiveArrays;
    const smjni::java_method<void, jTypes, jobjectArray, jstringArray, jthrowableArray, jclassArray, jByteBufferArray> m_objectArrays;

};


inline Types_class::Types_class(JNIEnv * env):
    simple_java_class(env),
    m_primitives(env, *this, "primitives"),
    m_nullablePrimitives(env, *this, "nullablePrimitives"),
    m_objects(env, *this, "objects"),
    m_primitiveArrays(env, *this, "primitiveArrays"),
    m_objectArrays(env, *this, "objectArrays")
{}

#endif

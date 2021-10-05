#ifndef HEADER_TYPES_CLASS_H_INCLUDED
#define HEADER_TYPES_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class Types_class : public smjni::java_runtime::simple_java_class<jTypes>
{
public:
    Types_class(JNIEnv * env);

    void nullablePrimitives(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jByte> & byte, const smjni::auto_java_ref<jChar> & c_har, const smjni::auto_java_ref<jBool> & b_ool, const smjni::auto_java_ref<jShort> & s_hort, const smjni::auto_java_ref<jInteger> & i_nt, const smjni::auto_java_ref<jLong> & l_ong, const smjni::auto_java_ref<jFloat> & f_loat, const smjni::auto_java_ref<jDouble> & d_ouble) const
        { m_nullablePrimitives(env, self, byte, c_har, b_ool, s_hort, i_nt, l_ong, f_loat, d_ouble); }
    void objectArrays(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jobjectArray> & objArray, const smjni::auto_java_ref<jstringArray> & stringArray, const smjni::auto_java_ref<jthrowableArray> & throwableArray, const smjni::auto_java_ref<jclassArray> & clsArray, const smjni::auto_java_ref<jByteBufferArray> & byteBufferArray) const
        { m_objectArrays(env, self, objArray, stringArray, throwableArray, clsArray, byteBufferArray); }
    void objects(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jobject> & obj, const smjni::auto_java_ref<jstring> & string, const smjni::auto_java_ref<jthrowable> & throwable, const smjni::auto_java_ref<jclass> & cls, const smjni::auto_java_ref<jByteBuffer> & byteBuffer) const
        { m_objects(env, self, obj, string, throwable, cls, byteBuffer); }
    void primitiveArrays(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, const smjni::auto_java_ref<jbyteArray> & byteArray, const smjni::auto_java_ref<jcharArray> & charArray, const smjni::auto_java_ref<jbooleanArray> & boolArray, const smjni::auto_java_ref<jshortArray> & shortArray, const smjni::auto_java_ref<jintArray> & intArray, const smjni::auto_java_ref<jlongArray> & longArray, const smjni::auto_java_ref<jfloatArray> & floatArray, const smjni::auto_java_ref<jdoubleArray> & doubleArray) const
        { m_primitiveArrays(env, self, byteArray, charArray, boolArray, shortArray, intArray, longArray, floatArray, doubleArray); }
    void primitives(JNIEnv * env, const smjni::auto_java_ref<jTypes> & self, jbyte byte, jchar c_har, jboolean b_ool, jshort s_hort, jint i_nt, jlong l_ong, jfloat f_loat, jdouble d_ouble) const
        { m_primitives(env, self, byte, c_har, b_ool, s_hort, i_nt, l_ong, f_loat, d_ouble); }
private:
    const smjni::java_method<void, jTypes, jByte, jChar, jBool, jShort, jInteger, jLong, jFloat, jDouble> m_nullablePrimitives;
    const smjni::java_method<void, jTypes, jobjectArray, jstringArray, jthrowableArray, jclassArray, jByteBufferArray> m_objectArrays;
    const smjni::java_method<void, jTypes, jobject, jstring, jthrowable, jclass, jByteBuffer> m_objects;
    const smjni::java_method<void, jTypes, jbyteArray, jcharArray, jbooleanArray, jshortArray, jintArray, jlongArray, jfloatArray, jdoubleArray> m_primitiveArrays;
    const smjni::java_method<void, jTypes, jbyte, jchar, jboolean, jshort, jint, jlong, jfloat, jdouble> m_primitives;

};


inline Types_class::Types_class(JNIEnv * env):
    simple_java_class(env),
    m_nullablePrimitives(env, *this, "nullablePrimitives"),
    m_objectArrays(env, *this, "objectArrays"),
    m_objects(env, *this, "objects"),
    m_primitiveArrays(env, *this, "primitiveArrays"),
    m_primitives(env, *this, "primitives")
{}

#endif

#ifndef HEADER_ACLASS_CLASS_H_INCLUDED
#define HEADER_ACLASS_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class AClass_class : public smjni::java_runtime::simple_java_class<jAClass>
{
public:
    AClass_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    smjni::local_java_ref<jAClass> ctor(JNIEnv * env, const smjni::auto_java_ref<jthrowable> & throwable) const
        { return m_ctor(env, *this, throwable); }
    void func(JNIEnv * env, const smjni::auto_java_ref<jAClass> & self, jchar c) const
        { m_func(env, self, c); }
    jint staticFunc(JNIEnv * env, jchar c) const
        { return m_staticFunc(env, *this, c); }
    jint get_constField(JNIEnv * env, const smjni::auto_java_ref<jAClass> & self) const
        { return m_constField.get(env, self); }
    smjni::local_java_ref<jstring> get_field(JNIEnv * env, const smjni::auto_java_ref<jAClass> & self) const
        { return m_field.get(env, self); }
    void set_field(JNIEnv * env, const smjni::auto_java_ref<jAClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_field.set(env, self, value); }
    jlong get_staticConstField(JNIEnv * env) const
        { return m_staticConstField.get(env, *this); }
    smjni::local_java_ref<jstring> get_staticField(JNIEnv * env) const
        { return m_staticField.get(env, *this); }
    void set_staticField(JNIEnv * env, const smjni::auto_java_ref<jstring> & value) const
        { m_staticField.set(env, *this, value); }
private:
    static jint JNICALL nativeFunc(JNIEnv *, jAClass, jlong l);
    static jdoubleArray JNICALL staticNativeFunc(JNIEnv *, jclass, jcharArray ca);

    const smjni::java_constructor<jAClass, jthrowable> m_ctor;
    const smjni::java_method<void, jAClass, jchar> m_func;
    const smjni::java_static_method<jint, jAClass, jchar> m_staticFunc;
    const smjni::java_field<jint, jAClass> m_constField;
    const smjni::java_field<jstring, jAClass> m_field;
    const smjni::java_static_field<jlong, jAClass> m_staticConstField;
    const smjni::java_static_field<jstring, jAClass> m_staticField;

};


inline AClass_class::AClass_class(JNIEnv * env):
    simple_java_class(env),
    m_ctor(env, *this),
    m_func(env, *this, "func"),
    m_staticFunc(env, *this, "staticFunc"),
    m_constField(env, *this, "constField"),
    m_field(env, *this, "field"),
    m_staticConstField(env, *this, "staticConstField"),
    m_staticField(env, *this, "staticField")
{}

inline void AClass_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("nativeFunc", nativeFunc),
        bind_native("staticNativeFunc", staticNativeFunc),
    });
}

#endif

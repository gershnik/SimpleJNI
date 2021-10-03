#ifndef HEADER_STATICS_CLASS_H_INCLUDED
#define HEADER_STATICS_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class Statics_class : public smjni::java_runtime::simple_java_class<jStatics>
{
public:
    Statics_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    jint func(JNIEnv * env) const
        { return m_func(env, *this); }
    jint get_constProp(JNIEnv * env) const
        { return m_constProp.get(env, *this); }
    smjni::local_java_ref<jstring> get_fieldMutableProp(JNIEnv * env) const
        { return m_fieldMutableProp.get(env, *this); }
    void set_fieldMutableProp(JNIEnv * env, const smjni::auto_java_ref<jstring> & value) const
        { m_fieldMutableProp.set(env, *this, value); }
    smjni::local_java_ref<jstring> get_fieldProp(JNIEnv * env) const
        { return m_fieldProp.get(env, *this); }
    smjni::local_java_ref<jstring> get_lateInitProp(JNIEnv * env) const
        { return m_lateInitProp.get(env, *this); }
    void set_lateInitProp(JNIEnv * env, const smjni::auto_java_ref<jstring> & value) const
        { m_lateInitProp.set(env, *this, value); }
    smjni::local_java_ref<jstring> get_regularMutableProp(JNIEnv * env) const
        { return m_regularMutableProp.get(env, *this); }
    void set_regularMutableProp(JNIEnv * env, const smjni::auto_java_ref<jstring> & value) const
        { m_regularMutableProp.set(env, *this, value); }
    smjni::local_java_ref<jstring> get_regularProp(JNIEnv * env) const
        { return m_regularProp.get(env, *this); }
private:
    static jobject JNICALL ext(JNIEnv *, jclass);

    const smjni::java_static_method<jint, jStatics> m_func;
    const smjni::java_static_field<jint, jStatics> m_constProp;
    const smjni::java_static_field<jstring, jStatics> m_fieldMutableProp;
    const smjni::java_static_field<jstring, jStatics> m_fieldProp;
    const smjni::java_static_field<jstring, jStatics> m_lateInitProp;
    const smjni::java_static_field<jstring, jStatics> m_regularMutableProp;
    const smjni::java_static_field<jstring, jStatics> m_regularProp;

};


inline Statics_class::Statics_class(JNIEnv * env):
    simple_java_class(env),
    m_func(env, *this, "func"),
    m_constProp(env, *this, "constProp"),
    m_fieldMutableProp(env, *this, "fieldMutableProp"),
    m_fieldProp(env, *this, "fieldProp"),
    m_lateInitProp(env, *this, "lateInitProp"),
    m_regularMutableProp(env, *this, "regularMutableProp"),
    m_regularProp(env, *this, "regularProp")
{}

inline void Statics_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("ext", ext),
    });
}

#endif

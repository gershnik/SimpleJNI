#ifndef HEADER_ANENUM_CLASS_H_INCLUDED
#define HEADER_ANENUM_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class AnEnum_class : public smjni::java_runtime::simple_java_class<jAnEnum>
{
public:
    AnEnum_class(JNIEnv * env);

    jint get_field(JNIEnv * env, const smjni::auto_java_ref<jAnEnum> & self) const
        { return m_field.get(env, self); }
    void set_field(JNIEnv * env, const smjni::auto_java_ref<jAnEnum> & self, jint value) const
        { m_field.set(env, self, value); }
    smjni::local_java_ref<jAnEnum> get_FIRST(JNIEnv * env) const
        { return m_FIRST.get(env, *this); }
    smjni::local_java_ref<jAnEnum> get_SECOND(JNIEnv * env) const
        { return m_SECOND.get(env, *this); }
    smjni::local_java_ref<jAnEnum> get_THIRD(JNIEnv * env) const
        { return m_THIRD.get(env, *this); }
private:
    const smjni::java_field<jint, jAnEnum> m_field;
    const smjni::java_static_field<jAnEnum, jAnEnum> m_FIRST;
    const smjni::java_static_field<jAnEnum, jAnEnum> m_SECOND;
    const smjni::java_static_field<jAnEnum, jAnEnum> m_THIRD;

};


inline AnEnum_class::AnEnum_class(JNIEnv * env):
    simple_java_class(env),
    m_field(env, *this, "field"),
    m_FIRST(env, *this, "FIRST"),
    m_SECOND(env, *this, "SECOND"),
    m_THIRD(env, *this, "THIRD")
{}

#endif

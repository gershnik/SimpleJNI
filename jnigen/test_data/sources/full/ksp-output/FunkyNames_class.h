#ifndef HEADER_FUNKYNAMES_CLASS_H_INCLUDED
#define HEADER_FUNKYNAMES_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class FunkyNames_class : public smjni::java_runtime::simple_java_class<jFunkyNames>
{
public:
    FunkyNames_class(JNIEnv * env);

    void func_with_funky_name(JNIEnv * env, const smjni::auto_java_ref<jFunkyNames> & self, const smjni::auto_java_ref<jintArray> & ___, const smjni::auto_java_ref<jMiddleArray> & ___1) const
        { m_func_with_funky_name(env, self, ___, ___1); }
    smjni::local_java_ref<jstring> get_hello_world(JNIEnv * env, const smjni::auto_java_ref<jFunkyNames> & self) const
        { return m_hello_world.get(env, self); }
    void set_hello_world(JNIEnv * env, const smjni::auto_java_ref<jFunkyNames> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_hello_world.set(env, self, value); }
    smjni::local_java_ref<jstring> get_some_prop(JNIEnv * env, const smjni::auto_java_ref<jFunkyNames> & self) const
        { return m_some_prop.get(env, self); }
private:
    const smjni::java_method<void, jFunkyNames, jintArray, jMiddleArray> m_func_with_funky_name;
    const smjni::java_field<jstring, jFunkyNames> m_hello_world;
    const smjni::java_field<jstring, jFunkyNames> m_some_prop;

};


inline FunkyNames_class::FunkyNames_class(JNIEnv * env):
    simple_java_class(env),
    m_func_with_funky_name(env, *this, "func with funky name"),
    m_hello_world(env, *this, "hello world"),
    m_some_prop(env, *this, "some+prop")
{}

#endif

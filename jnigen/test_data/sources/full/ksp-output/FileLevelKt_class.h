#ifndef HEADER_FILELEVELKT_CLASS_H_INCLUDED
#define HEADER_FILELEVELKT_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class FileLevelKt_class : public smjni::java_runtime::simple_java_class<jFileLevelKt>
{
public:
    FileLevelKt_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    void func(JNIEnv * env) const
        { m_func(env, *this); }
    void kotlinFun(JNIEnv * env) const
        { m_kotlinFun(env, *this); }
    jint get_field(JNIEnv * env) const
        { return m_field.get(env, *this); }
private:
    static void JNICALL nativeFun(JNIEnv *, jclass);

    const smjni::java_static_method<void, jFileLevelKt> m_func;
    const smjni::java_static_method<void, jFileLevelKt> m_kotlinFun;
    const smjni::java_static_field<jint, jFileLevelKt> m_field;

};


inline FileLevelKt_class::FileLevelKt_class(JNIEnv * env):
    simple_java_class(env),
    m_func(env, *this, "func"),
    m_kotlinFun(env, *this, "kotlinFun"),
    m_field(env, *this, "field")
{}

inline void FileLevelKt_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("nativeFun", nativeFun),
    });
}

#endif

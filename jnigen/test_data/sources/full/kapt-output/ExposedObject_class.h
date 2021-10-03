#ifndef HEADER_EXPOSEDOBJECT_CLASS_H_INCLUDED
#define HEADER_EXPOSEDOBJECT_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class ExposedObject_class : public smjni::java_runtime::simple_java_class<jExposedObject>
{
public:
    ExposedObject_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    jint get_field(JNIEnv * env) const
        { return m_field.get(env, *this); }
    void kotlinFun(JNIEnv * env, const smjni::auto_java_ref<jExposedObject> & self) const
        { m_kotlinFun(env, self); }
    void staticKotlinFun(JNIEnv * env) const
        { m_staticKotlinFun(env, *this); }
private:
    static void JNICALL nativeFun(JNIEnv *, jExposedObject);
    static void JNICALL staticNativeFun(JNIEnv *, jclass);

    const smjni::java_static_field<jint, jExposedObject> m_field;
    const smjni::java_method<void, jExposedObject> m_kotlinFun;
    const smjni::java_static_method<void, jExposedObject> m_staticKotlinFun;

};


inline ExposedObject_class::ExposedObject_class(JNIEnv * env):
    simple_java_class(env),
    m_field(env, *this, "field"),
    m_kotlinFun(env, *this, "kotlinFun"),
    m_staticKotlinFun(env, *this, "staticKotlinFun")
{}

inline void ExposedObject_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("nativeFun", nativeFun),
        bind_native("staticNativeFun", staticNativeFun),
    });
}

#endif

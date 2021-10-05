#ifndef HEADER_EXTENSION_CLASS_H_INCLUDED
#define HEADER_EXTENSION_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class Extension_class : public smjni::java_runtime::simple_java_class<jExtension>
{
public:
    Extension_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    void extended(JNIEnv * env, const smjni::auto_java_ref<jExtension> & self, const smjni::auto_java_ref<jInner> & _this_extended) const
        { m_extended(env, self, _this_extended); }
private:
    static void JNICALL nativeExtended(JNIEnv *, jExtension, jInner _this_nativeExtended);

    const smjni::java_method<void, jExtension, jInner> m_extended;

};


inline Extension_class::Extension_class(JNIEnv * env):
    simple_java_class(env),
    m_extended(env, *this, "extended")
{}

inline void Extension_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("nativeExtended", nativeExtended),
    });
}

#endif

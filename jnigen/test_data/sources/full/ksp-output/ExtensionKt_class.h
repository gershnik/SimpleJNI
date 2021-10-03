#ifndef HEADER_EXTENSIONKT_CLASS_H_INCLUDED
#define HEADER_EXTENSIONKT_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class ExtensionKt_class : public smjni::java_runtime::simple_java_class<jExtensionKt>
{
public:
    ExtensionKt_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    void extended(JNIEnv * env, const smjni::auto_java_ref<jExtension> & receiver) const
        { m_extended(env, *this, receiver); }
private:
    static void JNICALL nativeExtended(JNIEnv *, jclass, jExtension receiver);

    const smjni::java_static_method<void, jExtensionKt, jExtension> m_extended;

};


inline ExtensionKt_class::ExtensionKt_class(JNIEnv * env):
    simple_java_class(env),
    m_extended(env, *this, "extended")
{}

inline void ExtensionKt_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("nativeExtended", nativeExtended),
    });
}

#endif

#ifndef HEADER_FULLCLASS_CLASS_H_INCLUDED
#define HEADER_FULLCLASS_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class FullClass_class : public smjni::java_runtime::simple_java_class<jFullClass>
{
public:
    FullClass_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    smjni::local_java_ref<jMiddle> func(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, jdouble d, jfloat f, const smjni::auto_java_ref<jthrowable> & ex) const
        { return m_func(env, self, d, f, ex); }
    void func_with_funky_name(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jobject> & a, const smjni::auto_java_ref<jobject> & b) const
        { m_func_with_funky_name(env, self, a, b); }
    smjni::local_java_ref<jFullClass> ctor(JNIEnv * env, const smjni::auto_java_ref<jstring> & str, jint i) const
        { return m_ctor(env, *this, str, i); }
    smjni::local_java_ref<jstring> getRegularProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularProp(env, self); }
    smjni::local_java_ref<jstring> getRegularMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularMutableProp(env, self); }
    void setRegularMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & arg) const
        { m_setRegularMutableProp(env, self, arg); }
    smjni::local_java_ref<jstring> get_fieldProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_fieldProp.get(env, self); }
    smjni::local_java_ref<jstring> get_fieldMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_fieldMutableProp.get(env, self); }
    void set_fieldMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_fieldMutableProp.set(env, self, value); }
    smjni::local_java_ref<jstring> get_lateInitProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_lateInitProp.get(env, self); }
    void set_lateInitProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_lateInitProp.set(env, self, value); }
private:
    static jobject JNICALL ext(JNIEnv *, jFullClass, jchar c, jboolean b);

    const smjni::java_method<jMiddle, jFullClass, jdouble, jfloat, jthrowable> m_func;
    const smjni::java_method<void, jFullClass, jobject, jobject> m_func_with_funky_name;
    const smjni::java_constructor<jFullClass, jstring, jint> m_ctor;
    const smjni::java_method<jstring, jFullClass> m_getRegularProp;
    const smjni::java_method<jstring, jFullClass> m_getRegularMutableProp;
    const smjni::java_method<void, jFullClass, jstring> m_setRegularMutableProp;
    const smjni::java_field<jstring, jFullClass> m_fieldProp;
    const smjni::java_field<jstring, jFullClass> m_fieldMutableProp;
    const smjni::java_field<jstring, jFullClass> m_lateInitProp;

};


inline FullClass_class::FullClass_class(JNIEnv * env):
    simple_java_class(env),
    m_func(env, *this, "func"),
    m_func_with_funky_name(env, *this, "func with funky name"),
    m_ctor(env, *this),
    m_getRegularProp(env, *this, "getRegularProp"),
    m_getRegularMutableProp(env, *this, "getRegularMutableProp"),
    m_setRegularMutableProp(env, *this, "setRegularMutableProp"),
    m_fieldProp(env, *this, "fieldProp"),
    m_fieldMutableProp(env, *this, "fieldMutableProp"),
    m_lateInitProp(env, *this, "lateInitProp")
{}

inline void FullClass_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("ext", ext),
    });
}

#endif

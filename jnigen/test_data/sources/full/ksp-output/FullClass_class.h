#ifndef HEADER_FULLCLASS_CLASS_H_INCLUDED
#define HEADER_FULLCLASS_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class FullClass_class : public smjni::java_runtime::simple_java_class<jFullClass>
{
public:
    FullClass_class(JNIEnv * env);

    void register_methods(JNIEnv * env) const;

    smjni::local_java_ref<jFullClass> ctor(JNIEnv * env, const smjni::auto_java_ref<jstring> & str, jint i) const
        { return m_ctor(env, *this, str, i); }
    void func(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, jdouble d, jfloat f) const
        { m_func1(env, self, d, f); }
    smjni::local_java_ref<jMiddle> func(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, jdouble d, jfloat f, const smjni::auto_java_ref<jthrowable> & ex) const
        { return m_func(env, self, d, f, ex); }
    smjni::local_java_ref<jstring> getRegularMutablePropWithGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularMutablePropWithGetter(env, self); }
    smjni::local_java_ref<jstring> getRegularMutablePropWithGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularMutablePropWithGetterAndSetter(env, self); }
    smjni::local_java_ref<jstring> getRegularMutablePropWithOnlyGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularMutablePropWithOnlyGetter(env, self); }
    smjni::local_java_ref<jstring> getRegularMutablePropWithOnlyGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularMutablePropWithOnlyGetterAndSetter(env, self); }
    smjni::local_java_ref<jstring> getRegularPropWithGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularPropWithGetter(env, self); }
    smjni::local_java_ref<jstring> getRegularPropWithOnlyGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_getRegularPropWithOnlyGetter(env, self); }
    void setRegularMutablePropWithGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & _set___) const
        { m_setRegularMutablePropWithGetterAndSetter(env, self, _set___); }
    void setRegularMutablePropWithOnlyGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & _set___) const
        { m_setRegularMutablePropWithOnlyGetterAndSetter(env, self, _set___); }
    void setRegularMutablePropWithOnlySetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & _set___) const
        { m_setRegularMutablePropWithOnlySetter(env, self, _set___); }
    void setRegularMutablePropWithSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & _set___) const
        { m_setRegularMutablePropWithSetter(env, self, _set___); }
    void v_irtual(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { m_v_irtual(env, self); }
    template<typename ClassType> void v_irtual(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const java_class<ClassType> & classForNonVirtualCall) const
        { m_v_irtual.call_non_virtual(env, self, classForNonVirtualCall); }
    smjni::local_java_ref<jstring> get_fieldMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_fieldMutableProp.get(env, self); }
    void set_fieldMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_fieldMutableProp.set(env, self, value); }
    smjni::local_java_ref<jstring> get_fieldProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_fieldProp.get(env, self); }
    smjni::local_java_ref<jstring> get_lateInitProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_lateInitProp.get(env, self); }
    void set_lateInitProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_lateInitProp.set(env, self, value); }
    smjni::local_java_ref<jstring> get_regularMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularMutableProp.get(env, self); }
    void set_regularMutableProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_regularMutableProp.set(env, self, value); }
    smjni::local_java_ref<jstring> get_regularMutablePropWithGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularMutablePropWithGetter.get(env, self); }
    void set_regularMutablePropWithGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_regularMutablePropWithGetter.set(env, self, value); }
    smjni::local_java_ref<jstring> get_regularMutablePropWithGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularMutablePropWithGetterAndSetter.get(env, self); }
    void set_regularMutablePropWithGetterAndSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_regularMutablePropWithGetterAndSetter.set(env, self, value); }
    smjni::local_java_ref<jstring> get_regularMutablePropWithSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularMutablePropWithSetter.get(env, self); }
    void set_regularMutablePropWithSetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self, const smjni::auto_java_ref<jstring> & value) const
        { m_regularMutablePropWithSetter.set(env, self, value); }
    smjni::local_java_ref<jstring> get_regularProp(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularProp.get(env, self); }
    smjni::local_java_ref<jstring> get_regularPropWithGetter(JNIEnv * env, const smjni::auto_java_ref<jFullClass> & self) const
        { return m_regularPropWithGetter.get(env, self); }
private:
    static jobject JNICALL ext(JNIEnv *, jFullClass, jchar c, jboolean b);
    static jobject JNICALL extOverloaded(JNIEnv *, jFullClass, jchar c, jint i);
    static jobject JNICALL extOverloaded(JNIEnv *, jFullClass, jchar c, jdouble d);

    const smjni::java_constructor<jFullClass, jstring, jint> m_ctor;
    const smjni::java_method<void, jFullClass, jdouble, jfloat> m_func1;
    const smjni::java_method<jMiddle, jFullClass, jdouble, jfloat, jthrowable> m_func;
    const smjni::java_method<jstring, jFullClass> m_getRegularMutablePropWithGetter;
    const smjni::java_method<jstring, jFullClass> m_getRegularMutablePropWithGetterAndSetter;
    const smjni::java_method<jstring, jFullClass> m_getRegularMutablePropWithOnlyGetter;
    const smjni::java_method<jstring, jFullClass> m_getRegularMutablePropWithOnlyGetterAndSetter;
    const smjni::java_method<jstring, jFullClass> m_getRegularPropWithGetter;
    const smjni::java_method<jstring, jFullClass> m_getRegularPropWithOnlyGetter;
    const smjni::java_method<void, jFullClass, jstring> m_setRegularMutablePropWithGetterAndSetter;
    const smjni::java_method<void, jFullClass, jstring> m_setRegularMutablePropWithOnlyGetterAndSetter;
    const smjni::java_method<void, jFullClass, jstring> m_setRegularMutablePropWithOnlySetter;
    const smjni::java_method<void, jFullClass, jstring> m_setRegularMutablePropWithSetter;
    const smjni::java_method<void, jFullClass> m_v_irtual;
    const smjni::java_field<jstring, jFullClass> m_fieldMutableProp;
    const smjni::java_field<jstring, jFullClass> m_fieldProp;
    const smjni::java_field<jstring, jFullClass> m_lateInitProp;
    const smjni::java_field<jstring, jFullClass> m_regularMutableProp;
    const smjni::java_field<jstring, jFullClass> m_regularMutablePropWithGetter;
    const smjni::java_field<jstring, jFullClass> m_regularMutablePropWithGetterAndSetter;
    const smjni::java_field<jstring, jFullClass> m_regularMutablePropWithSetter;
    const smjni::java_field<jstring, jFullClass> m_regularProp;
    const smjni::java_field<jstring, jFullClass> m_regularPropWithGetter;

};


inline FullClass_class::FullClass_class(JNIEnv * env):
    simple_java_class(env),
    m_ctor(env, *this),
    m_func1(env, *this, "func"),
    m_func(env, *this, "func"),
    m_getRegularMutablePropWithGetter(env, *this, "getRegularMutablePropWithGetter"),
    m_getRegularMutablePropWithGetterAndSetter(env, *this, "getRegularMutablePropWithGetterAndSetter"),
    m_getRegularMutablePropWithOnlyGetter(env, *this, "getRegularMutablePropWithOnlyGetter"),
    m_getRegularMutablePropWithOnlyGetterAndSetter(env, *this, "getRegularMutablePropWithOnlyGetterAndSetter"),
    m_getRegularPropWithGetter(env, *this, "getRegularPropWithGetter"),
    m_getRegularPropWithOnlyGetter(env, *this, "getRegularPropWithOnlyGetter"),
    m_setRegularMutablePropWithGetterAndSetter(env, *this, "setRegularMutablePropWithGetterAndSetter"),
    m_setRegularMutablePropWithOnlyGetterAndSetter(env, *this, "setRegularMutablePropWithOnlyGetterAndSetter"),
    m_setRegularMutablePropWithOnlySetter(env, *this, "setRegularMutablePropWithOnlySetter"),
    m_setRegularMutablePropWithSetter(env, *this, "setRegularMutablePropWithSetter"),
    m_v_irtual(env, *this, "virtual"),
    m_fieldMutableProp(env, *this, "fieldMutableProp"),
    m_fieldProp(env, *this, "fieldProp"),
    m_lateInitProp(env, *this, "lateInitProp"),
    m_regularMutableProp(env, *this, "regularMutableProp"),
    m_regularMutablePropWithGetter(env, *this, "regularMutablePropWithGetter"),
    m_regularMutablePropWithGetterAndSetter(env, *this, "regularMutablePropWithGetterAndSetter"),
    m_regularMutablePropWithSetter(env, *this, "regularMutablePropWithSetter"),
    m_regularProp(env, *this, "regularProp"),
    m_regularPropWithGetter(env, *this, "regularPropWithGetter")
{}

inline void FullClass_class::register_methods(JNIEnv * env) const
{
    register_natives(env, {
        bind_native("ext", ext),
        bind_native("extOverloaded", (jobject (JNICALL *)(JNIEnv *, jFullClass, jchar, jint))extOverloaded),
        bind_native("extOverloaded", (jobject (JNICALL *)(JNIEnv *, jFullClass, jchar, jdouble))extOverloaded),
    });
}

#endif

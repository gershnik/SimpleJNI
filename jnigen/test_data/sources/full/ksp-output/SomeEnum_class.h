#ifndef HEADER_SOMEENUM_CLASS_H_INCLUDED
#define HEADER_SOMEENUM_CLASS_H_INCLUDED


//THIS FILE IS AUTO-GENERATED. DO NOT EDIT

#include "type_mapping.h"

class SomeEnum_class : public smjni::java_runtime::simple_java_class<jSomeEnum>
{
public:
    SomeEnum_class(JNIEnv * env);

    smjni::local_java_ref<jSomeEnum> get_EAST(JNIEnv * env) const
        { return m_EAST.get(env, *this); }
    smjni::local_java_ref<jSomeEnum> get_NORTH(JNIEnv * env) const
        { return m_NORTH.get(env, *this); }
    smjni::local_java_ref<jSomeEnum> get_SOUTH(JNIEnv * env) const
        { return m_SOUTH.get(env, *this); }
    smjni::local_java_ref<jSomeEnum> get_WEST(JNIEnv * env) const
        { return m_WEST.get(env, *this); }
private:
    const smjni::java_static_field<jSomeEnum, jSomeEnum> m_EAST;
    const smjni::java_static_field<jSomeEnum, jSomeEnum> m_NORTH;
    const smjni::java_static_field<jSomeEnum, jSomeEnum> m_SOUTH;
    const smjni::java_static_field<jSomeEnum, jSomeEnum> m_WEST;

};


inline SomeEnum_class::SomeEnum_class(JNIEnv * env):
    simple_java_class(env),
    m_EAST(env, *this, "EAST"),
    m_NORTH(env, *this, "NORTH"),
    m_SOUTH(env, *this, "SOUTH"),
    m_WEST(env, *this, "WEST")
{}

#endif

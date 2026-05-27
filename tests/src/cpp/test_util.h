#ifndef HEADER_TEST_UTIL_H
#define HEADER_TEST_UTIL_H

#include "generated/all_classes.h"

#define CONCAT_TOKEN(foo, bar) CONCAT_TOKEN_IMPL(foo, bar)
#define CONCAT_TOKEN_IMPL(foo, bar) foo ## bar
#define STRINGIFY(name) STRINGIFY_IMPL(name)
#define STRINGIFY_IMPL(name) #name

#define NATIVE_PROLOG  try {
#define NATIVE_EPILOG  } \
                       catch(std::exception & ex)\
                       {\
                           java_exception::translate(env, ex);\
                       }

std::string demangle(const char* name);

class AssertionError : public smjni::java_runtime::simple_java_class<jAssertionError>
{
public:
    AssertionError(JNIEnv * env):
        simple_java_class(env),
        ctor(env, *this)
    {}

    smjni::java_constructor<jAssertionError, jstring> ctor;
};

typedef smjni::java_class_table<AssertionError, JNIGEN_ALL_GENERATED_CLASSES> java_classes;


#endif

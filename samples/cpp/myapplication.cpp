#include <smjni/smjni.h>
#include <generated/all_classes.h>

using namespace smjni;

#define NATIVE_PROLOG  try {
#define NATIVE_EPILOG  } \
                       catch(java_exception & ex) \
                       { \
                           ex.raise(env);\
                       }\
                       catch(std::exception & ex)\
                       {\
                           java_exception::translate(env, ex);\
                       }

using java_classes = smjni::java_class_table<JNIGEN_ALL_GENERATED_CLASSES>;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    try
    {
        jni_provider::init(vm);
        JNIEnv * env = jni_provider::get_jni();
        java_runtime::init(env);

        NATIVE_PROLOG
            java_classes::init(env);

            return JNI_VERSION_1_6;
        NATIVE_EPILOG
    }
    catch(std::exception & ex)
    {
        //If we are here there is no way to communicate with
        //Java - something really bad happened.
        //Let's just log and report failure
        fprintf(stderr, "%s\n", ex.what());

    }
    return 0;
}

jint JNICALL MainActivity_class::nativeFunction(JNIEnv *, jMainActivity, jbyte b) {
    return 1;
}
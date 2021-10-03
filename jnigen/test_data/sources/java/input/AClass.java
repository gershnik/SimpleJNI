
import smjni.jnigen.ExposeToNative;
import smjni.jnigen.CalledByNative;

@ExposeToNative
class AClass {
    @CalledByNative
    AClass(Throwable throwable) {

    }

    native int nativeFunc(long l);
    static native double[] staticNativeFunc(char[] ca);

    @CalledByNative
    void func(char c) {

    }

    @CalledByNative
    static int staticFunc(char c) {
        return 0;
    }

    @CalledByNative
    final int constField = 1;

    @CalledByNative
    String field;

    @CalledByNative
    static final long staticConstField = 1;

    @CalledByNative
    static String staticField;
}
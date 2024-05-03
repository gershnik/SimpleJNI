import smjni.jnigen.ExposeToNative;
import smjni.jnigen.CalledByNative;

@ExposeToNative
enum AnEnum {
    @CalledByNative
    FIRST(5),
    @CalledByNative
    SECOND(6),
    @CalledByNative
    THIRD(7);

    AnEnum(int i) {
        field = i;
    }

    @CalledByNative
    int field;
}
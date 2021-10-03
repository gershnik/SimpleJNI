import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative

@ExposeToNative
class Statics {

    companion object {

        @JvmStatic
        external fun ext() : Any

        @CalledByNative
        @JvmStatic
        fun func() : Int  {
            return 0
        }

        @CalledByNative
        @JvmStatic
        val regularProp : String = "abc"

        @CalledByNative
        @JvmStatic
        var regularMutableProp : String = "abc"

        @CalledByNative
        @JvmField
        val fieldProp : String = "abc"

        @CalledByNative
        @JvmField
        var fieldMutableProp : String = "abc"

        @CalledByNative
        lateinit var lateInitProp : String

        @CalledByNative
        const val constProp = 1
    }
}
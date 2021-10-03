import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative

@ExposeToNative
class FunkyNames {

    @CalledByNative
    fun `func with funky name`(`α+β`: IntArray, `א-ב`: Array<Middle>) {}

    @CalledByNative
    var `hello world` : String = "abc"

    @CalledByNative
    @JvmField
    val `some+prop` : String = "abc"

}
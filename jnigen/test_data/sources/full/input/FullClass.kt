
import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative

@ExposeToNative
open class Middle

open class Top : Middle() {
    open fun virtual() {

    }
}

@ExposeToNative
class FullClass @CalledByNative constructor(str: String, i: Int) : Top() {

    external fun ext(c: Char, b: Boolean) : Any

    external fun extOverloaded(c: Char, i: Int) : Any
    external fun extOverloaded(c: Char, d: Double) : Any

    @CalledByNative
    fun func(d: Double, f: Float, ex: Throwable) : Middle = this

    @CalledByNative
    fun func(d: Double, f: Float) {}

    @CalledByNative(allowNonVirtualCall = true)
    override fun virtual() {
        super.virtual()
    }

    @CalledByNative
    val regularProp : String = "abc"

    @CalledByNative
    val regularPropWithGetter : String = "abc"
        @CalledByNative get

    val regularPropWithOnlyGetter : String = "abc"
        @CalledByNative get

    @CalledByNative
    var regularMutableProp : String = "abc"

    @CalledByNative
    var regularMutablePropWithGetter : String = "abc"
        @CalledByNative get

    @CalledByNative
    var regularMutablePropWithSetter : String = "abc"
        @CalledByNative set

    @CalledByNative
    var regularMutablePropWithGetterAndSetter : String = "abc"
        @CalledByNative get
        @CalledByNative set

    var regularMutablePropWithOnlyGetter : String = "abc"
        @CalledByNative get

    var regularMutablePropWithOnlySetter : String = "abc"
        @CalledByNative set

    var regularMutablePropWithOnlyGetterAndSetter : String = "abc"
        @CalledByNative get
        @CalledByNative set

    @CalledByNative
    @JvmField
    val fieldProp : String = "abc"

    @CalledByNative
    @JvmField
    var fieldMutableProp : String = "abc"

    @CalledByNative
    lateinit var lateInitProp : String
}



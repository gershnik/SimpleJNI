@file:ExposeToNative

import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative

@CalledByNative
fun func() {}

external fun nativeFun()

@CalledByNative
fun kotlinFun() {}

@CalledByNative
@JvmField
val field = 1

object NonExposed {
    external fun notExposedFun()

    @CalledByNative
    fun nonExposedFun() {}
}

@ExposeToNative
object ExposedObject {
    external fun nativeFun()

    @JvmStatic
    external fun staticNativeFun()

    @CalledByNative
    fun kotlinFun() {}

    @CalledByNative
    @JvmStatic
    fun staticKotlinFun() {}

    @CalledByNative
    @JvmField
    val field = 1
}

@ExposeToNative
enum class SomeEnum {
    @CalledByNative NORTH,
    @CalledByNative SOUTH,
    @CalledByNative WEST,
    @CalledByNative EAST
}
@file:ExposeToNative

import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative

@ExposeToNative
class Extension {

    @ExposeToNative
    class Inner {

    }

    @CalledByNative
    fun Inner.extended() {}

    external fun Inner.nativeExtended()

}

@CalledByNative
fun Extension.extended() {}

external fun Extension.nativeExtended()
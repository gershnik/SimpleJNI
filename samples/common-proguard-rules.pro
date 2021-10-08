-keepclasseswithmembers class * {
   @smjni.jnigen.CalledByNative <methods>;
}

-keepclasseswithmembers class * {
   @smjni.jnigen.CalledByNative <fields>;
}

-keepclasseswithmembers class * {
    native <methods>;
}

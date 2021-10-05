@file:ExposeToNative

import smjni.jnigen.ExposeToNative

@ExposeToNative
class ExposedClass

@ExposeToNative
interface ExposedInterface

@ExposeToNative
enum class ExposedEnum

val exposedObject = @ExposeToNative object {}


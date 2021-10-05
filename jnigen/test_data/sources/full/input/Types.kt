package sources.full_class.input

import smjni.jnigen.CalledByNative
import smjni.jnigen.ExposeToNative
import java.nio.ByteBuffer

class Unmapped {}

@ExposeToNative
class Types {

    @CalledByNative
    fun primitives(byte: Byte, char: Char, bool: Boolean, short: Short, int: Int, long: Long, float: Float, double: Double) {}

    @CalledByNative
    fun nullablePrimitives(byte: Byte?, char: Char?, bool: Boolean?, short: Short?, int: Int?, long: Long?, float: Float?, double: Double?) {}

    @CalledByNative
    fun objects(obj: Any, string: String, throwable: Throwable, cls: Class<Types>, byteBuffer: ByteBuffer) {}

    @CalledByNative
    fun primitiveArrays(byteArray: ByteArray, charArray: CharArray, boolArray: BooleanArray, shortArray: ShortArray,
                        intArray: IntArray, longArray: LongArray, floatArray: FloatArray, doubleArray: DoubleArray) {}

    @CalledByNative
    fun objectArrays(objArray: Array<Any>, stringArray: Array<String>, throwableArray: Array<Throwable>,
                     clsArray: Array<Class<*>>, byteBufferArray: Array<ByteBuffer>) {}

//    @CalledByNative
//    fun unmapped(arg: Unmapped) {}
}
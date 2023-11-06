/*
 Copyright 2021 SimpleJNI Contributors

 Incorporates work covered by the following copyright
 Copyright 2019 SmJNI Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package smjni.tests;

import smjni.jnigen.CalledByNative;
import smjni.jnigen.ExposeToNative;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;


@ExposeToNative(className="TestSimpleJNI")
public class TestSimpleJNI  {

    @ExposeToNative(typeName="jBase", className="Base")
    static class Base
    {
        Base(int val)
        {
            value = val;
        }

        @CalledByNative
        static int staticMethod(int val)
        {
            return val;
        }

        @CalledByNative(allowNonVirtualCall = true)
        int instanceMethod(int val)
        {
            return val + 1;
        }

        @CalledByNative
        int value;
        @CalledByNative
        static int staticValue = 15;
    }

    @ExposeToNative(typeName="jDerived", className="Derived")
    static class Derived extends Base
    {
        @CalledByNative
        public Derived(int val)
        {
            super(val);
        }

        @Override
        int instanceMethod(int val)
        {
            return val + 2;
        }
    }

    public static void main(String[] args) {
        System.loadLibrary("smjnitests");
        System.exit(testMain(args));
    }

    public static int androidMain(String[] args) {
        System.loadLibrary("smjnitests");
        return testMain(args);
    }

    private static native int testMain(String[] args);

    @CalledByNative
    private static boolean testCallingNativeMethod() {
        return nativeMethodImplementation(true,
                     (byte)42,
                     'q',
                     (short)17,
                     64,
                     59L,
                     0.42f,
                     0.756,
                     "hello👶🏻",
                     new boolean[] { true, false },
                     new byte[] {3 ,4},
                     new char[] { 'm', 'p' },
                     new short[] { 9, 10 },
                     new int[] { 545, 212 },
                     new long[] { -1, -3 },
                     new float[] { 0.1f, 0.2f },
                     new double[] { 0.25, 0.26 },
                     new String[] { "abc" , "xyz"}
                );
    }

    
    private static native boolean nativeMethodImplementation(boolean bl, byte b, char c, short s, int i, long l, float f, double d, String str,
                                     boolean[] bla, byte[] ba, char[] ca, short[] sa, int[] ia, long[] la, float[] fa, double[] da, String[] stra);

    @CalledByNative
    private static void testPrimitiveArray()
    {
        int[] array = { 1, 2, 3, 4, 5 };
        char[] res = doTestPrimitiveArray(array);
        assertArrayEquals(array, new int[] {5, 4, 3, 2, 1});
        assertArrayEquals(res, new char[] { 'a', 'b'});
    }
    private static native char[] doTestPrimitiveArray(int[] array);

    @CalledByNative
    private static void testObjectArray()
    {
        String[] array = { "a", "b", "c", "d", "e" };
        String[] res = doTestObjectArray(array);
        assertArrayEquals(array, new String[] {"e", "d", "c", "b", "a"});
        assertArrayEquals(res, new String[] { "a", "a"});
    }

    private static native String[] doTestObjectArray(String[] array);

    @CalledByNative
    private static void testDirectBuffer()
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(5);
        buffer.put((byte)1);
        buffer.put((byte)2);
        buffer.put((byte)3);
        buffer.put((byte)4);
        buffer.put((byte)5);
        ByteBuffer res = doTestDirectBuffer(buffer);
        for(int i = 0; i < 5; ++i)
            assertEquals(5 - i, buffer.get(i));
        assertNotNull(res);
        assertEquals(2, res.capacity());
        for(int i = 0; i < 2; ++i)
            assertEquals(i + 1, res.get(i));
    }

    private static native ByteBuffer doTestDirectBuffer(ByteBuffer buffer);
}

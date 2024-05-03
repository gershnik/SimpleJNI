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

#include <smjni/smjni.h>

#include <doctest.h>

#include "test_util.h"

#include <thread>
#include <vector>
#include <algorithm>

using namespace smjni;

TEST_SUITE_BEGIN("integration");

TEST_CASE( "testCallingNativeMethod" )
{
    JNIEnv * env = jni_provider::get_jni();
    CHECK(java_classes::get<TestSimpleJNI>().testCallingNativeMethod(env) == java_true);
}

jboolean JNICALL TestSimpleJNI::nativeMethodImplementation(JNIEnv * env, jclass, jboolean bl, jbyte b, jchar c, jshort s, jint i, jlong l, jfloat f, jdouble d, jstring str,
                                     jbooleanArray bla, jbyteArray ba, jcharArray ca, jshortArray sa, jintArray ia, jlongArray la, jfloatArray fa, jdoubleArray da, jstringArray stra)
{
    NATIVE_PROLOG
        CHECK(bl);
        CHECK(42 == b);
        CHECK(u'q' == c);
        CHECK(17 == s);
        CHECK(64 == i);
        CHECK(59 == l);
        CHECK(0.42f == f);
        CHECK(0.756 == d);

        auto cppstr = java_string_to_cpp(env, str);
        CHECK("hello👶🏻" == cppstr);

        {
            java_array_access access(env, bla);
            auto expected = { java_true, java_false };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, ba);
            auto expected = { 3, 4 };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, ca);
            auto expected = { u'm', u'p' };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, sa);
            auto expected = { 9, 10 };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, ia);
            auto expected = { 545, 212 };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, la);
            auto expected = { -1, -3 };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, fa);
            auto expected = { 0.1f, 0.2f };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, da);
            auto expected = { 0.25, 0.26 };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));
        }

        {
            java_array_access access(env, stra);
            auto expected = { "abc" , "xyz" };
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end(),
                                   [env] (const auto & lhs, const auto & rhs) {
                return rhs == java_string_to_cpp(env, local_java_ref<jstring>(lhs));
            }));
        }

        return java_true;

    NATIVE_EPILOG
    return java_false;
}

static void doTestCallingJava()
{
    JNIEnv * env = jni_provider::get_jni();
    auto derived_class = java_classes::get<Derived>();
    auto base_class = java_classes::get<Base>();

    auto derived = derived_class.ctor(env, 42);

    CHECK(42 == base_class.get_value(env, derived));
    base_class.set_value(env, derived, -42);
    CHECK(-42 == base_class.get_value(env, derived));

    CHECK(15 == base_class.get_staticValue(env));
    base_class.set_staticValue(env, -15);
    CHECK(-15 == base_class.get_staticValue(env));
    base_class.set_staticValue(env, 15);

    CHECK(74 == base_class.staticMethod(env, 74));

    CHECK(5 == base_class.instanceMethod(env, derived, 3));

    CHECK(4 == base_class.instanceMethod(env, derived, base_class, 3));

    auto en = java_classes::get<AnEnum>();
    CHECK(0 == en.foo(env, en.get_FIRST(env)));
}

TEST_CASE( "testCallingJava" )
{
    doTestCallingJava();
    
    std::thread anotherThread(doTestCallingJava);
    
    anotherThread.join();
}


TEST_CASE( "testPrimitiveArray" )
{
    JNIEnv * env = jni_provider::get_jni();
    CHECK_NOTHROW(java_classes::get<TestSimpleJNI>().testPrimitiveArray(env));
}

jcharArray JNICALL TestSimpleJNI::doTestPrimitiveArray(JNIEnv * env, jclass, jintArray array)
{
    NATIVE_PROLOG

        {
            java_array_access access(env, array);
            CHECK(5 == access.size());
            auto expected = {1, 2, 3, 4, 5};
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));

            access[1] = 72; //this should be aborted
        }
        {
            java_array_access access(env, array);
            auto expected = {1, 2, 3, 4, 5};
            CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end()));

            std::reverse(access.begin(), access.end());
            access.commit(false);

            CHECK(access.begin() != nullptr);

            access.commit();
            CHECK(access.begin() == nullptr);
        }
        {
            java_array_access<jbyteArray> access(env, nullptr);
            CHECK(0 == access.size());
            CHECK(access.begin() == access.end());
        }

        std::vector<jchar> res = {u'a', u'b'};
        auto ret = java_array_create<jchar>(env, res.begin(), res.end());
        return ret.release();
    NATIVE_EPILOG
    return nullptr;
}

TEST_CASE( "testObjectArray" )
{
    JNIEnv * env = jni_provider::get_jni();
    CHECK_NOTHROW(java_classes::get<TestSimpleJNI>().testObjectArray(env));
}

jstringArray JNICALL TestSimpleJNI::doTestObjectArray(JNIEnv * env, jclass, jstringArray array)
{
    NATIVE_PROLOG

       java_array_access access(env, array);
       CHECK(5 == access.size());
       auto expected = {"a", "b", "c", "d", "e"};
       CHECK(std::equal(access.begin(), access.end(), expected.begin(), expected.end(),
                               [env] (const auto & lhs, const auto & rhs) {
            return rhs == java_string_to_cpp(env, local_java_ref<jstring>(lhs));
        }));

       std::reverse(access.begin(), access.end());
       auto string_class = java_class<jstring>(env, [] (JNIEnv * env) { return java_runtime::get_class<jstring>(env); });
       auto ret = java_array_create(env, string_class, 2, java_string_create(env, "a"));

       return ret.release();

    NATIVE_EPILOG
    return nullptr;
}

TEST_CASE( "testDirectBuffer" )
{
    JNIEnv * env = jni_provider::get_jni();
    CHECK_NOTHROW(java_classes::get<TestSimpleJNI>().testDirectBuffer(env));
}

jByteBuffer JNICALL TestSimpleJNI::doTestDirectBuffer(JNIEnv * env, jclass, jByteBuffer buffer)
{
    NATIVE_PROLOG

        java_direct_buffer<uint8_t> buf(env, buffer);
        auto expected = {1, 2, 3, 4, 5};
        CHECK(5 == buf.size());
        for(int i = 0; i < 5; ++i)
            CHECK(i + 1 == buf[i]);
        CHECK(std::equal(buf.begin(), buf.end(), expected.begin(), expected.end()));
        std::reverse(buf.begin(), buf.end());

        static uint8_t bytes[] = { 1, 2 }; //this must exist forever
        java_direct_buffer<uint8_t> ret(bytes, size_to_java(std::size(bytes)));
        return ret.to_java(env).release();

    NATIVE_EPILOG
    return nullptr;
}

TEST_SUITE_END();

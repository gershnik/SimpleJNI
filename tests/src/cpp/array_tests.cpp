/*
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

using namespace smjni;

TEST_SUITE_BEGIN("array");

template class smjni::java_array_access<jintArray>;
template class smjni::java_array_access<jobjectArray>;

#if __cpp_lib_ranges >= 201911L
    static_assert(std::ranges::contiguous_range<java_array_access<jintArray>>);
    static_assert(std::ranges::random_access_range<java_array_access<jobjectArray>>);
    static_assert(!std::ranges::contiguous_range<java_array_access<jobjectArray>>);
#endif

TEST_CASE( "testPrimitive" )
{
    JNIEnv * env = jni_provider::get_jni();

    {
        auto arr = java_array_create<jint>(env, 5);
        java_array_access<jintArray> acc(env, arr);
        CHECK(acc.size() == 5);
        CHECK(acc.at(0) == 0);
        acc[0] = 7;
        acc[4] = 5;
        CHECK(acc.at(0) == 7);
        CHECK(acc.front() == 7);
        CHECK(acc.back() == 5);

        java_array_access<jintArray> acc1 = std::move(acc);
        CHECK(acc.size() == 0);
        CHECK(acc1[0] == 7);

        std::vector<jint> vec;
        std::copy(acc1.begin(), acc1.end(), std::back_inserter(vec));
        CHECK(vec == std::vector<jint>{7, 0, 0, 0, 5});
    }

    {
        std::vector<jint> vec = {1, 2, 3};
        auto arr = java_array_create<jint>(env, vec.begin(), vec.end());
        java_array_access<jintArray> acc(env, arr);
        CHECK(std::equal(vec.begin(), vec.end(), acc.begin(), acc.end()));
    }
    #if __cpp_lib_ranges >= 201911L

        {
            std::vector<jint> vec = {1, 2, 3};
            auto arr = java_array_create<jint>(env, vec);
            java_array_access<jintArray> acc(env, arr);
            CHECK(std::ranges::equal(vec, acc));
        }

    #endif

    {
        std::vector<jint> vec = {1, 2, 3};
        auto arr = java_array_create<jint>(env, vec.begin(), vec.end());
        jint buf[2];
        java_array_get_region(env, arr, 1, 2, buf);
        CHECK(buf[0] == 2);
        CHECK(buf[1] == 3);
        buf[0] = 5;
        buf[1] = 6;
        java_array_set_region(env, arr, 1, 2, buf);
        java_array_access<jintArray> acc(env, arr);
        CHECK(acc[1] == 5);
        CHECK(acc[2] == 6);
        
    }

    #if __cpp_lib_ranges >= 201911L

        {
            std::vector<jint> vec = {1, 2, 3};
            auto arr = java_array_create<jint>(env, vec.begin(), vec.end());
            std::array<jint, 2> buf;
            java_array_get_region(env, arr, 1, buf);
            CHECK(buf[0] == 2);
            CHECK(buf[1] == 3);
            buf[0] = 5;
            buf[1] = 6;
            java_array_set_region(env, arr, 1, buf);
            java_array_access<jintArray> acc(env, arr);
            CHECK(acc[1] == 5);
            CHECK(acc[2] == 6);
            
        }
    #endif
}

TEST_CASE( "testObject" ) 
{
    JNIEnv * env = jni_provider::get_jni();

    {
        auto arr = java_array_create(env, java_runtime::object(), 5);
        java_array_access<jobjectArray> acc(env, arr);
        CHECK(acc.size() == 5);
        local_java_ref<jobject> obj = acc.at(0);
        CHECK(!obj);
        acc[0] = java_string_create(env, "abc");
        obj = acc.at(0);
        CHECK(obj);

        std::vector<std::string> buf;
        for(local_java_ref<jobject> obj: acc) {
            if (obj) {
                buf.push_back(java_string_to_cpp(env, obj));
            }
        }
        CHECK(buf == std::vector<std::string>{ "abc" });
    }

}

TEST_SUITE_END();
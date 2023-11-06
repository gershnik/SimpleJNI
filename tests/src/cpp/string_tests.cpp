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

TEST_SUITE_BEGIN("string");

TEST_CASE( "testString" )
{
    JNIEnv * env = jni_provider::get_jni();

    jchar chars[] = {u'h', u'e', u'l', u'l', u'o'};
    auto str1 = java_string_create(env, chars, size_to_java(std::size(chars)));
    CHECK(5 == java_string_get_length(env, str1));
    CHECK("hello" == java_string_to_cpp(env, str1));
    auto str2 = java_string_create(env, "hello");
    CHECK(5 == java_string_get_length(env, str2));
    CHECK("hello" == java_string_to_cpp(env, str2));
    auto str3 = java_string_create(env, std::string("hello"));
    CHECK(5 == java_string_get_length(env, str3));
    CHECK("hello" == java_string_to_cpp(env, str3));

    auto empty = java_string_create(env, nullptr);
    CHECK(0 == java_string_get_length(env, empty));
    CHECK("" == java_string_to_cpp(env, empty));

    CHECK(0 == java_string_get_length(env, nullptr));
    CHECK("" == java_string_to_cpp(env, nullptr));

    jchar buf[5] = {};
    java_string_get_region(env, str1, 1, 2, buf);
    CHECK(u'e' == buf[0]);
    CHECK(u'l' == buf[1]);
    CHECK(0 == buf[2]);

    java_string_access access(env, str2);
    CHECK(5 == access.size());
    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == access[i]);
    CHECK(std::equal(access.begin(), access.end(), std::begin(chars), std::end(chars)));

    java_string_access empty_access(env, empty);
    CHECK(0 == empty_access.size());
    CHECK(empty_access.begin() == empty_access.end());

    java_string_access null_access(env, nullptr);
    CHECK(0 == null_access.size());
    CHECK(null_access.begin() == null_access.end());
}

TEST_SUITE_END();

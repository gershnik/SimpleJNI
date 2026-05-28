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

#if __cpp_lib_ranges >= 201911L
    static_assert(std::ranges::contiguous_range<java_string_access>);
#endif

TEST_SUITE_BEGIN("string");

TEST_CASE( "string creation" )
{
    JNIEnv * env = jni_provider::get_jni();

    {
        auto empty = java_string_create(env, nullptr);
        CHECK(0 == java_string_get_length(env, empty));
        CHECK("" == java_string_to_cpp(env, empty));
    }

    {
        CHECK(0 == java_string_get_length(env, nullptr));
        CHECK("" == java_string_to_cpp(env, nullptr));
    }

    jchar chars[] = {u'h', u'e', u'l', u'l', u'o'};
    {
        auto str = java_string_create(env, chars, size_to_java(std::size(chars)));
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }

    {
        auto str = java_string_create(env, "hello");
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
    {
        auto str = java_string_create(env, u"hello");
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#if __cpp_char8_t >= 201811L

    {
        auto str = java_string_create(env, u8"hello");
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#endif
    {
        auto str = java_string_create(env, std::string("hello"));
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
    {
        auto str = java_string_create(env, std::u16string(u"hello"));
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#if __cpp_char8_t >= 201811L
    {
        auto str = java_string_create(env, std::u8string(u8"hello"));
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#endif
    
    {
        auto str = java_string_create(env, "hello world", 5);
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
    {
        auto str = java_string_create(env, u"hello world", 5);
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#if __cpp_char8_t >= 201811L
    {
        auto str = java_string_create(env, u8"hello world", 5);
        CHECK(5 == java_string_get_length(env, str));
        CHECK("hello" == java_string_to_cpp(env, str));
    }
#endif
    
    

#if __cpp_lib_ranges >= 201911L

    {
        auto str = java_string_create(env, std::vector{'a', 'b', 'c'});
        CHECK("abc" == java_string_to_cpp(env, str));
    }

    {
        auto str = java_string_create(env, std::vector{u'a', u'b', u'c'});
        CHECK("abc" == java_string_to_cpp(env, str));
    }

#if __cpp_char8_t >= 201811L

    {
        auto str = java_string_create(env, std::vector{u8'a', u8'b', u8'c'});
        CHECK("abc" == java_string_to_cpp(env, str));
    }

#endif

#endif

}

TEST_CASE( "string region" )
{
    JNIEnv * env = jni_provider::get_jni();
    auto str = java_string_create(env, "hello");

    {
        jchar buf[5] = {};
        java_string_get_region(env, str, 1, 2, buf);
        CHECK(u'e' == buf[0]);
        CHECK(u'l' == buf[1]);
        CHECK(0 == buf[2]);
    }
    {
        char16_t buf[5] = {};
        java_string_get_region(env, str, 1, 2, buf);
        CHECK(u'e' == buf[0]);
        CHECK(u'l' == buf[1]);
        CHECK(0 == buf[2]);
    }
    #if __cpp_lib_ranges >= 201911L
    {
        std::vector<jchar> buf(2);
        java_string_get_region(env, str, 1, buf);
        CHECK(u'e' == buf[0]);
        CHECK(u'l' == buf[1]);
    }
    {
        std::vector<char16_t> buf(2);
        java_string_get_region(env, str, 1, buf);
        CHECK(u'e' == buf[0]);
        CHECK(u'l' == buf[1]);
    }
    #endif

}

TEST_CASE( "string access" )
{
    JNIEnv * env = jni_provider::get_jni();

    char16_t chars[] = {u'h', u'e', u'l', u'l', u'o'};

    auto str = java_string_create(env, std::begin(chars), std::size(chars));
    auto empty = java_string_create(env, nullptr);

    java_string_access access(env, str);
    CHECK(5 == access.size());
    CHECK(!access.empty());

    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == access[i]);
    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == access.data()[i]);
    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == access.at(i));
    CHECK_THROWS_WITH_AS(access.at(5), 
                "index out of range", std::out_of_range);
    CHECK_THROWS_WITH_AS(access.at(-1), 
                "index out of range", std::out_of_range);

    CHECK(u'h' == access.front());
    CHECK(u'o' == access.back());

    CHECK(std::equal(access.begin(), access.end(), std::begin(chars), std::end(chars)));
    CHECK(std::equal(access.cbegin(), access.cend(), std::begin(chars), std::end(chars)));
    CHECK(std::equal(access.rbegin(), access.rend(), std::reverse_iterator(std::end(chars)), std::reverse_iterator(std::begin(chars))));
    CHECK(std::equal(access.crbegin(), access.crend(), std::reverse_iterator(std::end(chars)), std::reverse_iterator(std::begin(chars))));

    java_string_access empty_access(env, empty);
    CHECK(0 == empty_access.size());
    CHECK(empty_access.data());
    CHECK(empty_access.begin() == empty_access.end());

    java_string_access null_access(env, nullptr);
    CHECK(0 == null_access.size());
    CHECK(!null_access.data());
    CHECK(null_access.begin() == null_access.end());


    java_string_access moved = std::move(access);
    CHECK(5 == moved.size());
    CHECK(0 == access.size());
    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == moved.data()[i]);
    CHECK(!access.data());

    java_string_access moved2(env, nullptr);
    moved2 = std::move(moved);
    CHECK(5 == moved2.size());
    CHECK(0 == moved.size());
    for(int i = 0; i < 5; ++i)
        CHECK(chars[i] == moved2.data()[i]);
    CHECK(!moved.data());
}

TEST_SUITE_END();

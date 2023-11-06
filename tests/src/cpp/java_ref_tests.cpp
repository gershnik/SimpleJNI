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

#include "test_util.h"

#include <type_traits>

#include <iostream>

using namespace smjni;

struct TypeListEnd;

template<class H, class... Rest>
struct TypeList
{
    using Head = H;
    using Tail = TypeList<Rest...>;
};


template<class H>
struct TypeList<H>
{
    using Head = H;
    using Tail = TypeListEnd;
};


template<class TL, class Func>
void ForEach(Func func)
{
    if constexpr (std::is_same_v<TL, TypeListEnd>)
    {
        return;
    }
    else
    {
        func((typename TL::Head *)nullptr);
        ForEach<typename TL::Tail>(func);
    }
}

TEST_SUITE_BEGIN("javaref");

TEST_CASE( "testJavaRefProperties" )
{
    using RefTypes = TypeList<auto_java_ref<jobject>, local_java_ref<jobject>, global_java_ref<jobject>, weak_java_ref<jobject>>;

    ForEach<RefTypes>([] (auto * p) {

        using T = std::decay_t<decltype(*p)>;

        CHECK(std::is_default_constructible_v<T>);
        CHECK(std::is_nothrow_default_constructible_v<T>);

        CHECK((std::is_constructible_v<T, std::nullptr_t>));
        CHECK((std::is_nothrow_constructible_v<T, std::nullptr_t>));

        CHECK(std::is_copy_constructible_v<T>);
        CHECK(std::is_nothrow_move_constructible_v<T>);

        CHECK(std::is_copy_assignable_v<T>);
        CHECK(std::is_nothrow_move_assignable_v<T>);

        CHECK(std::is_nothrow_destructible_v<T>);
        CHECK(std::is_nothrow_swappable_v<T>);


    });

    CHECK((std::is_nothrow_constructible_v<auto_java_ref<jobject>, jobject>));
    CHECK_FALSE((std::is_constructible_v<auto_java_ref<jobject>, int *>));
    CHECK_FALSE((std::is_constructible_v<local_java_ref<jobject>, jobject>));
    CHECK_FALSE((std::is_constructible_v<global_java_ref<jobject>, jobject>));
    CHECK_FALSE((std::is_constructible_v<weak_java_ref<jobject>, jobject>));

    
    ForEach<RefTypes>([] (auto * p) {

        using T = std::decay_t<decltype(*p)>;

        ForEach<RefTypes>([] (auto * y) {

            using Y = std::decay_t<decltype(*y)>;

            INFO("Conversion to " << demangle(typeid(T).name()) << "\nfrom " << demangle(typeid(Y).name()));
            CHECK((std::is_nothrow_constructible_v<T, Y>));
            CHECK((std::is_nothrow_constructible_v<T, Y&&>));
            CHECK((std::is_nothrow_constructible_v<T, const Y&>));
            CHECK((std::is_nothrow_constructible_v<T, Y&>));
            CHECK((std::is_nothrow_constructible_v<T, const Y&&>));
            //ASSERT_TRUE((std::is_nothrow_constructible_v<auto_java_ref<jobject>, auto_java_ref<jstring>>));
            //ASSERT_TRUE((std::is_nothrow_constructible_v<auto_java_ref<jobject>, auto_java_ref<jstring>&&>));

        });

    });
}

TEST_SUITE_END();



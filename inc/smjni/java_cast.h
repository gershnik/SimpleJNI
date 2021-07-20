/*
 Copyright 2014 Smartsheet Inc.
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

#ifndef HEADER_JAVA_CAST_H_INCLUDED
#define	HEADER_JAVA_CAST_H_INCLUDED

#include <type_traits>

namespace smjni
{
    template<class From, class To>
    struct is_java_castable: std::integral_constant<bool,
         std::is_convertible_v<std::remove_pointer_t<From>, std::remove_pointer_t<jobject>> &&
         std::is_convertible_v<std::remove_pointer_t<From>, std::remove_pointer_t<To>>
    > {};

    template<class To>
    struct is_java_castable<jobject, To>: std::integral_constant<bool,
         std::is_convertible_v<std::remove_pointer_t<To>, std::remove_pointer_t<jobject>>
    > {};

    template<class From, class To>
    constexpr bool is_java_castable_v = is_java_castable<From, To>::value;

    template<typename Dest, typename Source>
    inline
    std::enable_if_t<is_java_castable_v<Source, Dest>,
    Dest> jstatic_cast(Source src) noexcept
    {
        return static_cast<Dest>(static_cast<jobject>(src));
    }
    
}

#define DEFINE_JAVA_CONVERSION(T1, T2) \
    namespace smjni\
    {\
        template<> struct is_java_castable<T1, T2> : std::true_type {};\
        template<> struct is_java_castable<T2, T1> : std::true_type {};\
    }


#endif

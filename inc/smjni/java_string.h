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

#ifndef HEADER_JAVA_STRING_INCLUDED
#define HEADER_JAVA_STRING_INCLUDED

#include <smjni/java_ref.h>
#include <smjni/java_exception.h>
#include <smjni/java_type_traits.h>
#include <smjni/utf_util.h>

#include <string>
#include <cstring>
#include <stdexcept>

#if __cpp_lib_ranges >= 201911L
    #include <ranges>
#endif

namespace smjni
{
    inline local_java_ref<jstring> java_string_create(JNIEnv * env, const jchar * str, jsize len)
    {
        jstring ret = env->NewString(str, len);
        if (!ret)
        {
            java_exception::check(env);
            THROW_JAVA_PROBLEM("cannot create java string");
        }
        return jattach(env, ret);
    }

    inline local_java_ref<jstring> java_string_create(JNIEnv * env, std::nullptr_t)
        { return java_string_create(env, (const jchar *)nullptr, 0); }

    inline local_java_ref<jstring> java_string_create(JNIEnv * env, const char16_t * str, size_t size)
        { return java_string_create(env, (const jchar *)str, size_to_java(size)); }
    inline local_java_ref<jstring> java_string_create(JNIEnv * env, const char16_t * str)
        { return java_string_create(env, (const jchar *)str, size_to_java(std::char_traits<char16_t>::length(str))); }
    
    local_java_ref<jstring> java_string_create(JNIEnv * env, const char * str, size_t size);
    
    inline local_java_ref<jstring> java_string_create(JNIEnv * env, const char * str)
        { return java_string_create(env, str, (str ? strlen(str) : 0)); }

    #if __cpp_char8_t >= 201811L
        inline local_java_ref<jstring> java_string_create(JNIEnv * env, const char8_t * str, size_t size) 
            { return java_string_create(env, (const char *)str, size); }
        inline local_java_ref<jstring> java_string_create(JNIEnv * env, const char8_t * str)
            { return java_string_create(env, (const char *)str, (str ? strlen((const char *)str) : 0)); }
    #endif

    #if __cpp_lib_ranges < 201911L
        inline local_java_ref<jstring> java_string_create(JNIEnv * env, const std::string & str)
            { return java_string_create(env, str.data(), str.size()); }
    #else
        template<std::ranges::contiguous_range R>
        requires(std::is_same_v<std::ranges::range_value_t<R>, jchar> ||
                 std::is_same_v<std::ranges::range_value_t<R>, char16_t>)
        inline local_java_ref<jstring> java_string_create(JNIEnv * env, const R & str)
            { return java_string_create(env, std::data(str), std::size(str)); }

        template<std::ranges::contiguous_range R>
        requires(std::is_same_v<std::ranges::range_value_t<R>, char> 
        #if __cpp_char8_t >= 201811L
                || std::is_same_v<std::ranges::range_value_t<R>, char8_t>
        #endif
        )
        inline local_java_ref<jstring> java_string_create(JNIEnv * env, const R & str)
            { return java_string_create(env, std::data(str), std::size(str)); }
    #endif
        
        
    inline jsize java_string_get_length(JNIEnv * env, const auto_java_ref<jstring> & str)
    {
        if (!str)
            return 0;
        jsize ret = env->GetStringLength(str.c_ptr());
        if (ret < 0)
        {
            java_exception::check(env);
            THROW_JAVA_PROBLEM("invalid string size");
        }
        return ret;
    }

    inline void java_string_get_region(JNIEnv * env, const auto_java_ref<jstring> & str, jsize start, jsize len, jchar * buf)
    {
        env->GetStringRegion(str.c_ptr(), start, len, buf);
        java_exception::check(env);
    }

    std::string java_string_to_cpp(JNIEnv * env, const auto_java_ref<jstring> & str);

    class java_string_access
    {
    public:
        typedef jchar element_type;
        
        typedef const element_type * iterator;
        typedef const element_type * const_iterator;
        typedef std::reverse_iterator<const element_type *> reverse_iterator;
        typedef std::reverse_iterator<const element_type *> const_reverse_iterator;
        typedef jsize size_type;
        typedef element_type value_type;
    public:
        java_string_access(JNIEnv * env, const auto_java_ref<jstring> & str):
            m_env(env),
            m_str(str.c_ptr()),
            m_length(java_string_get_length(env, str))
        {
            if (!str)
                return;
            m_data = env->GetStringChars(m_str, nullptr);
            if (!m_data)
            {
                java_exception::check(env);
                THROW_JAVA_PROBLEM("cannot access java string");
            }
        }
        java_string_access(const java_string_access &) = delete;
        java_string_access & operator=(const java_string_access &) = delete;

        java_string_access(java_string_access && src) noexcept:
            m_env(src.m_env),
            m_str(std::exchange(src.m_str, nullptr)),
            m_length(std::exchange(src.m_length, 0))
        {}

        java_string_access & operator=(java_string_access && src) noexcept
        {
            if (this != &src) 
            {
                this->~java_string_access();
                new (this) java_string_access(std::move(src));
            }
            return *this;
        }

        ~java_string_access()
        {
            if (m_data)
                m_env->ReleaseStringChars(m_str, m_data);
        }
        
        const element_type * begin() const noexcept
            { return m_data; }
        const element_type * cbegin() const noexcept
            { return m_data; }
        const element_type * end() const noexcept
            { return m_data + m_length; }
        const element_type * cend() const noexcept
            { return m_data + m_length; }
        const_reverse_iterator rbegin() const noexcept
            { return const_reverse_iterator(m_data + m_length); }
        const_reverse_iterator crbegin() const noexcept
            { return const_reverse_iterator(m_data + m_length); }
        const_reverse_iterator rend() const noexcept
            { return const_reverse_iterator(m_data); }
        const_reverse_iterator crend() const noexcept
            { return const_reverse_iterator(m_data); }

        const element_type * data() const noexcept
            { return m_data; }
        jsize size() const noexcept
            { return m_length; }
        bool empty() const noexcept
            { return m_length == 0; }
        const element_type & operator[](jsize idx) const noexcept
            { return m_data[idx]; }

        const element_type & at(jsize idx) const
        {
            if (idx < 0 || idx >= m_length)
                throw std::out_of_range("index out of range");
            return m_data[idx];
        }

        const element_type & front() const noexcept
            { return m_data[0]; }
        const element_type & back() const noexcept
            { return m_data[m_length - 1]; }
        
    private:
        JNIEnv * m_env = nullptr;
        jstring m_str = nullptr;
        jsize m_length = 0;
        const element_type * m_data = nullptr;
    };
}

#endif

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

#include <vector>
#include <iterator>

#include <smjni/java_string.h>
#include <smjni/java_exception.h>
#include <smjni/java_runtime.h>

using namespace smjni;

static constexpr char g_defaultWhat[] = "smjni::java_exception";
static constexpr char g_defaultWhatPrefix[] = "smjni::java_exception: ";
    
    

const char * java_exception::what() const noexcept
{ 
    if (m_what.empty())
    {
        try
        {
            m_what = do_what(); 
        }
        catch(std::exception & ex)
        {
            internal::do_log_error(ex, "exception while trying to produce exception description");
            //ignore
        }
    }
    return m_what.c_str();
}

std::string java_exception::do_what() const
{
    JNIEnv * jenv = jni_provider::get_jni();
    auto message = java_runtime::object().toString(jenv, m_throwable.c_ptr());
    if (!message)
        return g_defaultWhat;
    java_string_access messageAccess(jenv, message);
    std::string ret;
    ret.reserve(std::size(g_defaultWhatPrefix) - 1 + messageAccess.size());
    ret = g_defaultWhatPrefix;
    utf16_to_utf8(messageAccess.begin(), messageAccess.end(), std::back_inserter(ret));
    return ret;
}

void java_exception::translate(JNIEnv * env, const std::exception & ex)
{
    const char * message = ex.what();
    auto java_message = java_string_create(env, message);
    auto exception = java_runtime::throwable().ctor(env, java_message);
    java_exception::raise(env, exception);
}

#
# Copyright 2019 SmJNI Contributors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cmake_minimum_required(VERSION 3.16)

cmake_policy(SET CMP0135 NEW)

include(FetchContent)

FetchContent_Declare(doctest
    URL  https://raw.githubusercontent.com/doctest/doctest/v2.4.11/doctest/doctest.h
    DOWNLOAD_NO_EXTRACT TRUE
)

FetchContent_MakeAvailable(doctest)

add_library(smjnitests SHARED EXCLUDE_FROM_ALL)

target_link_libraries(smjnitests PRIVATE
    smjni::smjni
    $<$<PLATFORM_ID:Android>:log>
)

set_property(TARGET smjnitests PROPERTY CXX_STANDARD 17)
set_property(TARGET smjnitests PROPERTY CXX_STANDARD_REQUIRED ON)
set_property(TARGET smjnitests PROPERTY CXX_VISIBILITY_PRESET hidden)
set_property(TARGET smjnitests PROPERTY VISIBILITY_INLINES_HIDDEN ON)
set_property(TARGET smjnitests PROPERTY POSITION_INDEPENDENT_CODE ON)

target_include_directories(smjnitests
PRIVATE
    ${doctest_SOURCE_DIR}
)

target_compile_options(smjnitests 
PRIVATE 
    $<$<CXX_COMPILER_ID:MSVC>:/utf-8;/W4;/wd4100;/wd4127>
    $<$<CXX_COMPILER_ID:Clang>:-Wall;-Wextra;-Wno-unused-parameter>
    $<$<CXX_COMPILER_ID:AppleClang>:-Wall;-Wextra;-Wno-unused-parameter>
    $<$<CXX_COMPILER_ID:GNU>:-Wall;-Wextra;-Wno-unused-parameter;-Wno-unused-but-set-parameter>
)

target_link_options(smjnitests 
PRIVATE
    $<$<PLATFORM_ID:Android>:-Wl,--export-dynamic>
)

target_sources(smjnitests PRIVATE
    integration_tests.cpp
    java_ref_tests.cpp
    smjnitests.cpp
    string_tests.cpp
    test_util.h
    utf_tests.cpp
)


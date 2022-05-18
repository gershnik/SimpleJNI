#
# Copyright 2014 Smartsheet Inc.
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

cmake_minimum_required(VERSION 3.5)
project(smjni)

if (NOT ANDROID)
    set(JAVA_AWT_INCLUDE_PATH NotNeeded)
    set(JAVA_AWT_LIBRARY NotNeeded)
    find_package(JNI)

    set(JNI_INCLUDE_DIRS ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} CACHE PATH "JNI include dirs")
else()
    set(JNI_INCLUDE_DIRS "")
endif()


add_library(smjni STATIC
    src/stdpch.h
    src/java_exception.cpp
    src/java_externals.cpp
    src/java_field.cpp
    src/java_method.cpp
    src/java_runtime.cpp
    src/java_string.cpp
    src/jni_provider.cpp

    inc/smjni/config.h
    inc/smjni/ct_string.h
    inc/smjni/java_array.h
    inc/smjni/java_class_table.h
    inc/smjni/java_class.h
    inc/smjni/java_direct_buffer.h
    inc/smjni/java_exception.h
    inc/smjni/java_externals.h
    inc/smjni/java_field.h
    inc/smjni/java_frame.h
    inc/smjni/java_method.h
    inc/smjni/java_ref.h
    inc/smjni/java_runtime.h
    inc/smjni/java_string.h
    inc/smjni/java_type_traits.h
    inc/smjni/java_types.h
    inc/smjni/jni_provider.h
    inc/smjni/smjni.h
    inc/smjni/utf_util.h
)

set_property(TARGET smjni PROPERTY CXX_STANDARD 17)
set_property(TARGET smjni PROPERTY CXX_STANDARD_REQUIRED ON)
set_property(TARGET smjni PROPERTY CXX_VISIBILITY_PRESET hidden)
set_property(TARGET smjni PROPERTY VISIBILITY_INLINES_HIDDEN ON)
set_property(TARGET smjni PROPERTY POSITION_INDEPENDENT_CODE ON)

target_compile_options(smjni 
    PUBLIC 
    $<$<CXX_COMPILER_ID:MSVC>:/Zc:__cplusplus>

    PRIVATE
    $<$<CXX_COMPILER_ID:MSVC>:/W4;/wd4100;/wd4127>
    $<$<CXX_COMPILER_ID:Clang>:-Wall;-Wextra;-Wno-unused-parameter>
    $<$<CXX_COMPILER_ID:AppleClang>:-Wall;-Wextra;-Wno-unused-parameter>
    $<$<CXX_COMPILER_ID:GNU>:-Wall;-Wextra;-Wno-unused-parameter>
)

target_include_directories(smjni 

    PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/inc
    ${JNI_INCLUDE_DIRS}
)

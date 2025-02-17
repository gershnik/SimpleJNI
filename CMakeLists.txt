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

cmake_policy(SET CMP0076 NEW)  # target_sources() command converts relative paths to absolute

if(CMAKE_VERSION VERSION_LESS 3.21)
  get_property(not_top DIRECTORY PROPERTY PARENT_DIRECTORY)
  if(NOT not_top)
    set(PROJECT_IS_TOP_LEVEL true)
  endif()
endif()

# the following only works in top level so there is no need to check
# PROJECT_IS_TOP_LEVEL which will not work before project() call anyway
cmake_policy(SET CMP0091 NEW) # use MSVC_RUNTIME_LIBRARY property
if (NOT DEFINED CMAKE_MSVC_RUNTIME_LIBRARY)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
endif()

file(READ VERSION SMJNI_VERSION)
if (NOT SMJNI_VERSION)
    message(FATAL_ERROR "Cannot determine library version (VERSION file not found)")
endif()
string(STRIP ${SMJNI_VERSION} SMJNI_VERSION)

project(smjni VERSION ${SMJNI_VERSION} LANGUAGES CXX)

option(SMJNI_NO_TESTS "disable test targets" OFF)

if(NOT DEFINED JNI_INCLUDE_DIRS)
    if (NOT ANDROID)
        set(JAVA_AWT_INCLUDE_PATH NotNeeded)
        set(JAVA_AWT_LIBRARY NotNeeded)
        find_package(JNI)

        set(JNI_INCLUDE_DIRS ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} CACHE PATH "JNI include dirs")
    else()
        set(JNI_INCLUDE_DIRS "")
    endif()
endif()


add_library(smjni STATIC)
add_library(smjni::smjni ALIAS smjni)

set_target_properties(smjni PROPERTIES
    CXX_VISIBILITY_PRESET hidden
    VISIBILITY_INLINES_HIDDEN ON
    POSITION_INDEPENDENT_CODE ON
)

target_compile_features(smjni 
PUBLIC 
    cxx_std_17
)

set(SMJNI_HEADERS
    config.h
    ct_string.h
    java_array.h
    java_cast.h
    java_class_table.h
    java_class.h
    java_direct_buffer.h
    java_exception.h
    java_externals.h
    java_field.h
    java_frame.h
    java_method.h
    java_ref.h
    java_runtime.h
    java_string.h
    java_type_traits.h
    java_types.h
    jni_provider.h
    smjni.h
    utf_util.h
)

set(SMJNI_SOURCES
    java_exception.cpp
    java_externals.cpp
    java_field.cpp
    java_method.cpp
    java_runtime.cpp
    java_string.cpp
    jni_provider.cpp
)

foreach(header ${SMJNI_HEADERS})
    list(APPEND SMJNI_BUILD_HEADERS $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/inc/smjni/${header}>)
    list(APPEND SMJNI_INSTALL_HEADERS $<INSTALL_INTERFACE:include/smjni/${header}>)
    list(APPEND SMJNI_PROP_PUBLIC_HEADERS ${CMAKE_CURRENT_SOURCE_DIR}/inc/smjni/${header})    
endforeach()

foreach(source ${SMJNI_SOURCES})
    list(APPEND SMJNI_BUILD_SOURCES ${CMAKE_CURRENT_SOURCE_DIR}/src/${source})
endforeach()

target_sources(smjni
PUBLIC
    ${SMJNI_BUILD_HEADERS}
    ${SMJNI_INSTALL_HEADERS}
PRIVATE
    ${SMJNI_BUILD_SOURCES}
)

set_target_properties(smjni PROPERTIES PUBLIC_HEADER "${SMJNI_PROP_PUBLIC_HEADERS}")

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
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/inc>
    $<INSTALL_INTERFACE:include>  # <prefix>/include
    ${JNI_INCLUDE_DIRS}
)


include(cmake/install.cmake)

if (PROJECT_IS_TOP_LEVEL 
        AND IS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/tests
        AND IS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/jnigen
        AND NOT SMJNI_NO_TESTS)

    add_subdirectory(tests)

endif()

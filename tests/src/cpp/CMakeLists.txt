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

find_package(Java COMPONENTS Development REQUIRED)

include(FetchContent)

FetchContent_Declare(doctest
    URL  https://raw.githubusercontent.com/doctest/doctest/v2.4.11/doctest/doctest.h
    DOWNLOAD_NO_EXTRACT TRUE
)

FetchContent_MakeAvailable(doctest)

if (${CMAKE_SYSTEM_NAME} STREQUAL Android)

    set(ANDROID_SDK_DIR ${CMAKE_ANDROID_NDK}/../..)
    set(ADB ${ANDROID_SDK_DIR}/platform-tools/adb)
    set(ANDROID_TEST_DIR /data/local/tmp/smjni_test)
    set(GRADLE_BUILD_PROS -Pandroid.sdk=${ANDROID_SDK_DIR})

endif()

add_custom_target(test-native ALL)

set (TEST_SUFFIXES
    17
    20
)

set(CXX_STANDARD_17 17)
set(CXX_STANDARD_20 20)

foreach(TEST_SUFFIX ${TEST_SUFFIXES})

    set(TEST_TARGET_NAME smjnitests-${TEST_SUFFIX})

    add_library(${TEST_TARGET_NAME} SHARED EXCLUDE_FROM_ALL)

    add_dependencies(${TEST_TARGET_NAME} javabuild)

    target_link_libraries(${TEST_TARGET_NAME} PRIVATE
        smjni::smjni
        $<$<PLATFORM_ID:Android>:log>
    )

    set_target_properties(${TEST_TARGET_NAME} PROPERTIES
        CXX_STANDARD ${CXX_STANDARD_${TEST_SUFFIX}}
        CXX_STANDARD_REQUIRED ON
        CXX_EXTENSIONS OFF
        CXX_VISIBILITY_PRESET hidden
        VISIBILITY_INLINES_HIDDEN ON 
        POSITION_INDEPENDENT_CODE ON
    )

    target_include_directories(${TEST_TARGET_NAME}
    PRIVATE
        ${doctest_SOURCE_DIR}
    )

    target_compile_options(${TEST_TARGET_NAME} 
    PRIVATE 
        $<$<CXX_COMPILER_ID:MSVC>:/utf-8;/W4;/wd4100;/wd4127>
        $<$<CXX_COMPILER_ID:Clang>:-Wall;-Wextra;-Wno-unused-parameter>
        $<$<CXX_COMPILER_ID:AppleClang>:-Wall;-Wextra;-Wno-unused-parameter>
        $<$<CXX_COMPILER_ID:GNU>:-Wall;-Wextra;-Wno-unused-parameter;-Wno-unused-but-set-parameter>
    )

    target_link_options(${TEST_TARGET_NAME} 
    PRIVATE
        $<$<PLATFORM_ID:Android>:-Wl,--export-dynamic>
    )

    target_sources(${TEST_TARGET_NAME} PRIVATE
        array_tests.cpp
        integration_tests.cpp
        java_ref_tests.cpp
        smjnitests.cpp
        string_tests.cpp
        test_util.h
        utf_tests.cpp
    )

    if (${CMAKE_SYSTEM_NAME} STREQUAL Android)

        add_custom_target(test-native-${TEST_SUFFIX}
            DEPENDS ${TEST_TARGET_NAME}
            COMMAND ${ADB} shell mkdir -p ${ANDROID_TEST_DIR}
            COMMAND ${ADB} push smjnitests.dex $<TARGET_FILE:${TEST_TARGET_NAME}> ${ANDROID_TEST_DIR}/
            COMMAND ${ADB} shell ANDROID_DATA=${ANDROID_TEST_DIR} app_process 
                            -cp ${ANDROID_TEST_DIR}/smjnitests.dex 
                            -Djava.library.path=${ANDROID_TEST_DIR} 
                            -Dsmjni.cppver=${TEST_SUFFIX}
                            ${ANDROID_TEST_DIR} 
                            smjni.tests.TestSimpleJNI
            WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/../../build
        )

    else()

        if (Java_VERSION VERSION_GREATER_EQUAL 9)

            set(SMJNITEST_JAVA_ARGS "-XX:+CreateCoredumpOnCrash")

        endif()

        add_custom_target(test-native-${TEST_SUFFIX}
            DEPENDS ${TEST_TARGET_NAME}
            COMMAND ${Java_JAVA_EXECUTABLE} ${SMJNITEST_JAVA_ARGS}
                            -Djava.library.path=$<TARGET_FILE_DIR:${TEST_TARGET_NAME}>
                            -Dsmjni.cppver=${TEST_SUFFIX}
                            -jar smjnitests.jar
            WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/../../build
        )

    endif()

    add_dependencies(test-native test-native-${TEST_SUFFIX})

endforeach()


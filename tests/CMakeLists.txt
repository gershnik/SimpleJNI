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

if (${CMAKE_SYSTEM_NAME} STREQUAL Android)

    set(ANDROID_SDK_DIR ${CMAKE_ANDROID_NDK}/../..)
    set(ADB ${ANDROID_SDK_DIR}/platform-tools/adb)
    set(JAVATESTCODE_BUILD_ARGS -Pandroid.sdk=${ANDROID_SDK_DIR})

endif()

find_package(Java COMPONENTS Development REQUIRED)

include(FetchContent)

FetchContent_Declare(doctest
    URL  https://raw.githubusercontent.com/doctest/doctest/v2.4.11/doctest/doctest.h
    DOWNLOAD_NO_EXTRACT TRUE
)

FetchContent_MakeAvailable(doctest)


add_custom_target(build-jnigen  
    COMMAND ./gradlew -Pcode.version=SNAPSHOT publishToMavenLocal
    WORKING_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/../jnigen"
)

add_custom_target(test-jnigen
    COMMAND ./gradlew -Pcode.version=SNAPSHOT test
    WORKING_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/../jnigen"
)


add_custom_target(javatestcode  
    DEPENDS build-jnigen  
    COMMAND ./gradlew ${JAVATESTCODE_BUILD_ARGS} assemble
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
)

add_custom_target(test-native)

set (CXX_STANDARDS
    17
    20
)
if (NOT DEFINED TEST_MAX_CXX_STANDARD OR "${TEST_MAX_CXX_STANDARD}" GREATER_EQUAL 23)
    list(APPEND CXX_STANDARDS 23)
endif()

foreach(CXX_STANDARD ${CXX_STANDARDS})

    set(TEST_SUFFIX ${CXX_STANDARD})

    set(TEST_TARGET_NAME smjnitests-${TEST_SUFFIX})

    add_library(${TEST_TARGET_NAME} SHARED EXCLUDE_FROM_ALL)

    add_dependencies(${TEST_TARGET_NAME} javatestcode)

    target_link_libraries(${TEST_TARGET_NAME} PRIVATE
        smjni::smjni
        $<$<PLATFORM_ID:Android>:log>
    )

    set_target_properties(${TEST_TARGET_NAME} PROPERTIES
        CXX_STANDARD ${CXX_STANDARD}
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
        src/cpp/array_tests.cpp
        src/cpp/integration_tests.cpp
        src/cpp/java_ref_tests.cpp
        src/cpp/smjnitests.cpp
        src/cpp/string_tests.cpp
        src/cpp/test_util.h
        src/cpp/utf_tests.cpp
    )

    if (${CMAKE_SYSTEM_NAME} STREQUAL Android)

        set(ANDROID_TEST_DIR /data/local/tmp/smjni_test)

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
            WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/build
        )

    else()

        set(SMJNITEST_JAVA_ARGS)

        if (Java_VERSION VERSION_GREATER_EQUAL 9 AND WIN32)

            list(APPEND SMJNITEST_JAVA_ARGS "-XX:+CreateCoredumpOnCrash" )

        endif()

        if (Java_VERSION VERSION_GREATER_EQUAL 21)

            list(APPEND SMJNITEST_JAVA_ARGS "--enable-native-access=ALL-UNNAMED")

        endif()

        add_custom_target(test-native-${TEST_SUFFIX}
            DEPENDS ${TEST_TARGET_NAME}
            COMMAND ${Java_JAVA_EXECUTABLE} ${SMJNITEST_JAVA_ARGS}
                            -Djava.library.path=$<TARGET_FILE_DIR:${TEST_TARGET_NAME}>
                            -Dsmjni.cppver=${TEST_SUFFIX}
                            -jar smjnitests.jar
            WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/build
        )

    endif()

    add_dependencies(test-native test-native-${TEST_SUFFIX})

endforeach()




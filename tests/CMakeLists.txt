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

add_subdirectory("src/cpp" ${CMAKE_CURRENT_BINARY_DIR}/test)

add_custom_target(build-jnigen  
    COMMAND ./gradlew --no-daemon -Pcode.version=SNAPSHOT publishToMavenLocal
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/../jnigen
)

add_custom_target(test-jnigen ALL
    COMMAND ./gradlew --no-daemon -Pcode.version=SNAPSHOT test
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/../jnigen
)


if (${CMAKE_SYSTEM_NAME} STREQUAL Android)

    set(ANDROID_SDK_DIR ${CMAKE_ANDROID_NDK}/../..)
    set(ADB ${ANDROID_SDK_DIR}/platform-tools/adb)
    set(ANDROID_TEST_DIR /data/local/tmp/smjni_test)

    add_custom_target(javabuild  
        DEPENDS build-jnigen  
        COMMAND ./gradlew --no-daemon -PbuildDir=${CMAKE_CURRENT_BINARY_DIR}/java
            -Pandroid.sdk=${ANDROID_SDK_DIR}
            assemble
        WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
    )

    add_dependencies(smjnitests javabuild)

    add_custom_target(run-dex ALL
        DEPENDS javabuild smjnitests 
        COMMAND ${ADB} shell mkdir -p ${ANDROID_TEST_DIR}
        COMMAND ${ADB} push ${CMAKE_CURRENT_BINARY_DIR}/java/smjnitests.dex $<TARGET_FILE:smjnitests> ${ANDROID_TEST_DIR}/
        COMMAND ${ADB} shell 
                CLASSPATH=${ANDROID_TEST_DIR}/smjnitests.dex
                ANDROID_DATA=${ANDROID_TEST_DIR} 
                LD_LIBRARY_PATH=${ANDROID_TEST_DIR} 
                app_process ${ANDROID_TEST_DIR} smjni.tests.TestSimpleJNI
    )

else()

    find_package(Java COMPONENTS Development REQUIRED)

    add_custom_target(javabuild  
        DEPENDS build-jnigen  
        COMMAND ./gradlew --no-daemon -PbuildDir=${CMAKE_CURRENT_BINARY_DIR}/java assemble
        WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
    )

    add_dependencies(smjnitests javabuild)

    add_custom_target(run-jar ALL
        DEPENDS javabuild smjnitests 
        COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:smjnitests> ${CMAKE_CURRENT_BINARY_DIR}/java
        COMMAND ${CMAKE_COMMAND} -E env "LD_LIBRARY_PATH=${CMAKE_CURRENT_BINARY_DIR}/java:$ENV{LD_LIBRARY_PATH}" 
            ${Java_JAVA_EXECUTABLE} -jar smjnitests.jar
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/java
    )

endif()

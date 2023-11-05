#
# Copyright 2023 SmJNI Contributors
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

find_package (Python3 COMPONENTS Interpreter REQUIRED)

add_custom_target(javabuild  
    DEPENDS build-jnigen  
    COMMAND ./gradlew --no-daemon -PbuildDir=${CMAKE_CURRENT_BINARY_DIR}/gradle :app:compileDebugJavaWithJavac
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
)

add_dependencies(smjnitests javabuild)

add_custom_target(androidbuild ALL
    DEPENDS javabuild smjnitests 
    COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_CURRENT_LIST_DIR}/app/libs/${ANDROID_ABI}
    COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:smjnitests> ${CMAKE_CURRENT_LIST_DIR}/app/libs/${ANDROID_ABI}/$<TARGET_FILE_NAME:smjnitests>
    COMMAND ./gradlew --no-daemon -PbuildDir=${CMAKE_CURRENT_BINARY_DIR}/gradle :app:assembleDebug
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
)

set(ANDROID_SDK_DIR ${CMAKE_ANDROID_NDK}/../..)
set(ADB ${ANDROID_SDK_DIR}/platform-tools/adb)


add_custom_target(run-app ALL
    DEPENDS androidbuild smjnitests 
    COMMAND ${Python3_EXECUTABLE} run-app.py --sdk ${ANDROID_SDK_DIR} --outdir ${CMAKE_CURRENT_BINARY_DIR} 
        ${CMAKE_CURRENT_BINARY_DIR}/gradle/outputs/apk/debug/app-debug.apk
    WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
)


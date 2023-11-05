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
    COMMAND ${CMAKE_COMMAND} -E env "LD_LIBRARY_PATH=${CMAKE_CURRENT_BINARY_DIR}/java:$ENV{LD_LIBRARY_PATH}" ${Java_JAVA_EXECUTABLE} -jar smjnitests.jar
    WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/java
)

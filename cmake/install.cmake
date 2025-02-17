# Copyright 2025 SmJNI Contributors
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

include(GNUInstallDirs)
include(CMakePackageConfigHelpers)

install(
    TARGETS smjni
    EXPORT smjni
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/smjni
)

install(
    EXPORT smjni 
    NAMESPACE smjni:: 
    FILE smjni-exports.cmake 
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/smjni
)

configure_package_config_file(
        ${CMAKE_CURRENT_LIST_DIR}/smjni-config.cmake.in
        ${CMAKE_CURRENT_BINARY_DIR}/smjni-config.cmake
    INSTALL_DESTINATION
        ${CMAKE_INSTALL_LIBDIR}/smjni
)

write_basic_package_version_file(${CMAKE_CURRENT_BINARY_DIR}/smjni-config-version.cmake
    COMPATIBILITY SameMajorVersion
    ARCH_INDEPENDENT
)

install(
    FILES
        ${CMAKE_CURRENT_BINARY_DIR}/smjni-config.cmake
        ${CMAKE_CURRENT_BINARY_DIR}/smjni-config-version.cmake
    DESTINATION
        ${CMAKE_INSTALL_LIBDIR}/smjni
)

file(RELATIVE_PATH FROM_PCFILEDIR_TO_PREFIX ${CMAKE_INSTALL_FULL_DATAROOTDIR}/smjni ${CMAKE_INSTALL_PREFIX})
string(REGEX REPLACE "/+$" "" FROM_PCFILEDIR_TO_PREFIX "${FROM_PCFILEDIR_TO_PREFIX}") 

configure_file(
    ${CMAKE_CURRENT_LIST_DIR}/smjni.pc.in
    ${CMAKE_CURRENT_BINARY_DIR}/smjni.pc
    @ONLY
)

install(
    FILES
        ${CMAKE_CURRENT_BINARY_DIR}/smjni.pc
    DESTINATION
        ${CMAKE_INSTALL_DATAROOTDIR}/pkgconfig
)

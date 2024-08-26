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

pluginManagement {
    plugins {
        val gradleExtra = (gradle as ExtensionAware).extra
        val kotlinVersion by gradleExtra("1.9.24")
        val kspVersion by gradleExtra("1.0.20")
        val junitVersion by gradleExtra("5.11.0")
        val kotlinCompileTestingVersion by gradleExtra("1.6.0")
        val hamcrestVersion by gradleExtra("3.0")

        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

include(":processor")
include(":kprocessor")
include(":annotations")

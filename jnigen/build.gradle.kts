/*
 Copyright 2021 SimpleJNI Contributors

 Incorporates work covered by the following copyright
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

allprojects {
    project.version =
        if (rootProject.hasProperty("code.version")) {
            rootProject.property("code.version") as String
        } else if (rootProject.file("../VERSION").isFile()) {
            rootProject.file("../VERSION").readText(Charsets.UTF_8).trim()
        } else {
            throw RuntimeException("version cannot be obtained from properties or ../VERSION")
        }
}





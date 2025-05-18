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
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

val ver_props = java.util.Properties()
ver_props.load(java.io.FileInputStream(file("versions.properties")))


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "${ver_props["kotlin"]}")
            version("ksp", "${ver_props["kotlin"]}-${ver_props["ksp"]}")
            version("kotlinCompileTesting", "${ver_props["kotlinCompileTesting"]}")
            version("junit", "${ver_props["junit"]}")
            version("hamcrest", "${ver_props["hamcrest"]}")

            library("ksp-symbol-processing-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
            library("kotlin-compile-testing", "dev.zacsweers.kctfork","core").versionRef("kotlinCompileTesting")
            library("kotlin-compile-testing-ksp", "dev.zacsweers.kctfork", "ksp").versionRef("kotlinCompileTesting")
            library("kotlin-test-junit5","org.jetbrains.kotlin", "kotlin-test-junit5").versionRef("kotlin")
            library("junit-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")
            library("hamcrest", "org.hamcrest", "hamcrest").versionRef("hamcrest")


            plugin("kotlin", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
        }
    }
}

include(":processor")
include(":kprocessor")
include(":annotations")

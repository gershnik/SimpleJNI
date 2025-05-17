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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            val kotlinVersionStr = "1.9.25"
            version("kotlin", kotlinVersionStr)
            version("ksp", "$kotlinVersionStr-1.0.20")
            version("kotlinCompileTesting", "1.6.0")
            version("junit", "5.12.2")
            version("hamcrest", "3.0")

            library("ksp-symbol-processing-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
            library("kotlin-compile-testing", "com.github.tschuchortdev","kotlin-compile-testing").versionRef("kotlinCompileTesting")
            library("kotlin-compile-testing-ksp", "com.github.tschuchortdev", "kotlin-compile-testing-ksp").versionRef("kotlinCompileTesting")
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

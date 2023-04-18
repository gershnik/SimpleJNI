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
buildscript {
    val kotlin_version by extra("1.8.0")
    val junit_version by extra("5.8.2")

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

plugins {
    val kotlin_version: String by extra
    id("org.jetbrains.kotlin.jvm") version "$kotlin_version" apply false
}

fun getOurVersion(): String {

    if (project.hasProperty("code.version")) {

        return project.property("code.version") as String

    } else if (file("../.git").isDirectory()) {

        val tagStdOut = java.io.ByteArrayOutputStream()
        exec {
            commandLine("git", "describe", "--tags", "--abbrev=0")
            standardOutput = tagStdOut
        }
        return tagStdOut.toString().trim()

    }

    throw RuntimeException("version cannot be obtained from Git or properties")
}



allprojects {
    repositories {
        mavenCentral()
    }

    project.version = getOurVersion()
    project.group = "io.github.gershnik"
    val artifactIdPrefix by project.extra("smjni-jnigen-")
    val gitUrl by project.extra("git@github.com:gershnik/SimpleJNI.git")
    val licenseName by project.extra("The Apache License, Version 2.0")
    val licenseUrl by project.extra("http://www.apache.org/licenses/LICENSE-2.0.txt")
    val websiteUrl by project.extra("https://github.com/gershnik/SimpleJNI")
    val vcsUrl by project.extra("https://github.com/gershnik/SimpleJNI.git")
    val issueTrackerUrl by project.extra("https://github.com/gershnik/SimpleJNI/issues")

    val kotlinJvmTarget by project.extra(8)
}

tasks.register<Zip>("bundleCpp") {
    group = "publishing"
    from("../src") {
        include("**/*.cpp")
        include("**/*.h")
        into("src")
    }
    from("../inc") {
        include("**/*.h")
        into("inc")
    }
    from("../CMakeLists.txt",
         "../COPYRIGHT.txt",
         "../LICENSE.txt",
         "../NOTICE.txt",
         "../README.md")

    archiveFileName.set("SimpleJNI-${project.version}-cpp-only.zip")
    destinationDirectory.set(rootProject.buildDir)
}

tasks.register<Zip>("bundleJava") {
    group = "publishing"
    dependsOn("annotations:assemble", "processor:assemble", "kprocessor:assemble")

    from(File(rootProject.buildDir, "jnigen.jar"),
         File(rootProject.buildDir, "kjnigen.jar"),
         File(rootProject.buildDir, "jnigen-annotations.jar"))

    includeEmptyDirs = true
    archiveFileName.set("SimpleJNI-${project.version}-jnigen.zip")
    destinationDirectory.set(rootProject.buildDir)
}

tasks.withType<Javadoc> {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:none", "-quiet")
    }
}


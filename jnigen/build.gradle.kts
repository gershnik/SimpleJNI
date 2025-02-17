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

fun getOurVersion(): String {

    if (project.hasProperty("code.version")) {

        return project.property("code.version") as String

    } else if (file("../VERSION").isFile()) {

        return file("../VERSION").readText(Charsets.UTF_8).trim()
    }

    throw RuntimeException("version cannot be obtained from properties or ../VERSION")
}



allprojects {
    project.version = getOurVersion()
    project.group = "io.github.gershnik"
    val artifactIdPrefix by project.extra("smjni-jnigen-")
    val pomData by project.extra(mapOf(
        "gitUrl" to "git@github.com:gershnik/SimpleJNI.git",
        "licenseName" to "The Apache License, Version 2.0",
        "licenseUrl" to "http://www.apache.org/licenses/LICENSE-2.0.txt",
        "websiteUrl" to "https://github.com/gershnik/SimpleJNI",
        "vcsUrl" to "https://github.com/gershnik/SimpleJNI.git",
        "issueTrackerUrl" to "https://github.com/gershnik/SimpleJNI/issues",
        "developer" to "gershnik",
        "developerEmail" to "gershnik-maven@gershnik.info"
    ))

    val kotlinJvmTarget by project.extra(8)
    val javaTargetCompatibility by project.extra(JavaVersion.VERSION_1_8)

    tasks.withType<Javadoc> {
        options {
            this as StandardJavadocDocletOptions
            addStringOption("Xdoclint:none", "-quiet")
        }
    }
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
    from("../cmake") {
        include("**/*.cmake")
        include("**/*.in")
        into("cmake")
    }
    from("../CMakeLists.txt",
         "../VERSION",
         "../LICENSE",
         "../NOTICE",
         "../README.md",
         "../CHANGELOG.md")

    archiveFileName.set("SimpleJNI-${project.version}-cpp-only.zip")
    destinationDirectory.set(rootProject.layout.buildDirectory)
}

tasks.register<Zip>("bundleJava") {
    group = "publishing"
    dependsOn("annotations:assemble", "processor:assemble", "kprocessor:assemble")

    from(rootProject.layout.buildDirectory.file("jnigen.jar").get().asFile,
         rootProject.layout.buildDirectory.file("kjnigen.jar").get().asFile,
         rootProject.layout.buildDirectory.file("jnigen-annotations.jar").get().asFile)

    includeEmptyDirs = true
    archiveFileName.set("SimpleJNI-${project.version}-jnigen.zip")
    destinationDirectory.set(rootProject.layout.buildDirectory)
}




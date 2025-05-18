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

plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

val libraryPomName by project.extra("SimpleJNI Code Generation Annotations")
val libraryDescription by project.extra("Annotations used by SimpleJNI JniGen annotation processor to generate C++ code")
val javaTargetCompatibility: JavaVersion by project.extra

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = javaTargetCompatibility
}

sourceSets {
    val main by getting
    main.java.srcDirs("src")
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = libraryPomName
        attributes["Implementation-Version"] = project.version
    }

    archiveFileName.set("jnigen-annotations.jar")
    destinationDirectory.set(rootProject.layout.buildDirectory)
}

tasks.register<Jar>("sourceJar") {
    val main by sourceSets
    from(main.allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

apply(plugin="common-publishing")



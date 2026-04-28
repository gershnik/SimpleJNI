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
    id("common-publishing")
}

commonPublishing {
    libraryPomName.set("SimpleJNI Code Generation Annotations")
    libraryDescription.set("Annotations used by SimpleJNI JniGen annotation processor to generate C++ code")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(JVM_TARGET)
    options.compilerArgs.add("-Xlint:-options")
}

sourceSets {
    val main by getting
    main.java.srcDirs("src")
}

tasks.jar {
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



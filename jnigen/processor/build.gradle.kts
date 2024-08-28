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
    alias(libs.plugins.kotlin)
    id("maven-publish")
    id("signing")
}

val libraryPomName by project.extra("SimpleJNI Code Generator")
val libraryDescription by project.extra("Annotation processor that generates SimpleJNI C++ code from Java annotations")
val kotlinJvmTarget: Int by project.extra
val javaTargetCompatibility: JavaVersion by project.extra

java {
    targetCompatibility = javaTargetCompatibility
}

kotlin {
    jvmToolchain(kotlinJvmTarget)
}

dependencies {
    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.hamcrest)
    testImplementation(project(":annotations"))
    testRuntimeOnly(libs.junit.jupiter.engine)
}


sourceSets {
    val main by getting
    main.kotlin.srcDirs("src")
    main.resources.srcDirs("res")
    val test by getting
    test.kotlin.srcDirs("test/src")
    test.resources.srcDirs("test/res")
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen {false}
    systemProperty("test.data.location", File(rootProject.projectDir, "test_data"))
    systemProperty("test.working.dir", project.layout.buildDirectory.file("test-output").get().asFile)
    //testLogging.showStandardStreams = true
    testLogging.showExceptions = true
    testLogging.showStackTraces = true
    testLogging.showCauses = true
    testLogging.setExceptionFormat("full")
}

tasks.create<Test>("generateTestData"){
    group = "verification"
    val test by sourceSets
    testClassesDirs = test.output.classesDirs
    classpath = test.runtimeClasspath
    outputs.upToDateWhen { false }

    useJUnitPlatform {
        includeTags("GENERATOR")
    }
    systemProperty("test.data.location", File(rootProject.projectDir, "test_data"))
    systemProperty("test.working.dir", project.layout.buildDirectory.file("test-output").get().asFile)
    environment("JNIGEN_ENABLE_TEST_GENERATION", "true")

    reports {
        html.required.set(false)
        junitXml.required.set(false)
    }
}


tasks.jar {
    manifest {
        attributes["Implementation-Title"] = libraryPomName
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = "smjni.jnigen.Runner"
    }
    metaInf { from("META-INF") }

    archiveFileName.set("jnigen.jar")
    destinationDirectory.set(rootProject.layout.buildDirectory)
}

tasks.register<Jar>("sourceJar") {
    val main by sourceSets
    from(main.allJava, main.kotlin)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

apply(from="../publishing.gradle.kts")







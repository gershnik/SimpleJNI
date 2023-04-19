/*
 Copyright 2021 SimpleJNI Contributors

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
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("signing")
}

val libraryPomName by project.extra("SimpleJNI KSP Code Generator")
val libraryDescription by project.extra("KSP annotation processor that generates SimpleJNI C++ code from Java annotations")
val kotlinJvmTarget: Int by project.extra
val javaTargetCompatibility: JavaVersion by project.extra
val kotlinVersion: String by (gradle as ExtensionAware).extra
val kspVersion: String by (gradle as ExtensionAware).extra
val junitVersion: String by (gradle as ExtensionAware).extra
val kotlinCompileTestingVersion: String by (gradle as ExtensionAware).extra
val hamcrestVersion: String by (gradle as ExtensionAware).extra

java {
    targetCompatibility = javaTargetCompatibility
}

kotlin {
    jvmToolchain(kotlinJvmTarget)
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$kotlinVersion-$kspVersion")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:$kotlinCompileTestingVersion")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:$kotlinCompileTestingVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation(project(":annotations"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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
    systemProperty("test.working.dir", File(buildDir, "test-output"))
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

    useJUnitPlatform() {
        includeTags("GENERATOR")
    }
    systemProperty("test.data.location", File(rootProject.projectDir, "test_data"))
    systemProperty("test.working.dir", File(buildDir, "test-output"))
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
    }
    metaInf { from("META-INF") }

    archiveFileName.set("kjnigen.jar")
    destinationDirectory.set(rootProject.buildDir)
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

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

val artifactIdPrefix = "smjni-jnigen-"
val pomData = mapOf(
    "gitUrl" to "git@github.com:gershnik/SimpleJNI.git",
    "licenseName" to "The Apache License, Version 2.0",
    "licenseUrl" to "https://www.apache.org/licenses/LICENSE-2.0.txt",
    "websiteUrl" to "https://github.com/gershnik/SimpleJNI",
    "vcsUrl" to "https://github.com/gershnik/SimpleJNI.git",
    "issueTrackerUrl" to "https://github.com/gershnik/SimpleJNI/issues",
    "developer" to "gershnik",
    "developerEmail" to "gershnik-maven@gershnik.info"
)
project.group = "io.github.gershnik"

interface CommonPublishingExtension {
    val libraryPomName: Property<String>
    val libraryDescription: Property<String>
}

val extension = extensions.create<CommonPublishingExtension>("commonPublishing")


plugins {
    id("maven-publish")
    id("signing")
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Implementation-Title"] = extension.libraryPomName.get()
        attributes["Implementation-Version"] = project.version
    }
}

configure<PublishingExtension> {
    repositories {
        if (project.hasProperty("customRepo")) {
            maven {
                name = "customRepo"
                url = java.net.URI(project.property("customRepo") as String)
            }
        }
    }
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("thePublication") {
                val libraryPomName = extension.libraryPomName.get()
                val libraryDescription = extension.libraryDescription.get()

                artifactId = "${artifactIdPrefix}${project.name}"
                from(components["java"])
                artifact(tasks["sourceJar"])
                artifact(tasks["javadocJar"])
                pom {
                    name.set(libraryPomName)
                    description.set(libraryDescription)
                    url.set(pomData["websiteUrl"])
                    licenses {
                        license {
                            name.set(pomData["licenseName"])
                            url.set(pomData["licenseUrl"])
                        }
                    }
                    scm {
                        connection.set(pomData["gitUrl"])
                        developerConnection.set(pomData["gitUrl"])
                        url.set(pomData["vcsUrl"])
                    }
                    developers {
                        developer {
                            id.set(pomData["develper"])
                            name.set(pomData["develper"])
                            email.set(pomData["developerEmail"])
                        }
                    }
                }
            }
        }
    }

    if (!(project.version as String).endsWith("SNAPSHOT")) {
        project.configure<SigningExtension> {
            if (project.hasProperty("signing.armoredKey")) {
                val signingKeyId = project.property("signing.keyId") as String
                val signingKey = project.property("signing.armoredKey") as String
                val signingPassword = project.property("signing.password") as String
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
            }
            sign(the<PublishingExtension>().publications["thePublication"])
        }
    }
}


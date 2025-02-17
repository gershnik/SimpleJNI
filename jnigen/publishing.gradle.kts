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
configure<PublishingExtension> {
    repositories {
        if (project.hasProperty("localRepo")) {
            maven {
                name = "localRepo"
                url = java.net.URI(project.property("localRepo") as String)
            }
        }
        if (project.hasProperty("ossrhUsername") && project.hasProperty("ossrhPassword")) {
            maven {
                name = "ossrh"
                url = java.net.URI(
                        if ((project.version as String).endsWith("SNAPSHOT"))
                            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                        else
                            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                )
                credentials {
                    username = project.property("ossrhUsername") as String
                    password = project.property("ossrhPassword") as String
                }
            }
        }
    }
    publications {
        create<MavenPublication>("thePublication") {
            val artifactIdPrefix: String by project.extra
            val libraryPomName: String by project.extra
            val libraryDescription: String by project.extra
            val pomData: Map<String, String> by project.extra

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
            println(signingKey)
            println(signingPassword)
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        }
        sign(the<PublishingExtension>().publications["thePublication"])
    }
}

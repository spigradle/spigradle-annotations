import kr.entree.spigradle.annotations.build.VersionTask

plugins {
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        create("spigradleAnnotations", MavenPublication::class) {
            from(components["java"])
            // NOTE: https://central.sonatype.org/publish/requirements/#sufficient-metadata
            pom {
                name.set("${project.group}:${project.name}")
                description.set("Annotations and processors for specify the main class.")
                url.set("https://github.com/spigradle/spigradle-annotations")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("entrypointkr")
                        name.set("Junhyung Im")
                        email.set("entrypointkr@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/spigradle/spigradle-annotations.git")
                    developerConnection.set("scm:git:ssh://github.com:spigradle/spigradle-annotations.git")
                    url.set("https://github.com/spigradle/spigradle-annotations/tree/master")
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatypeReleases"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername")?.toString()
                password = findProperty("ossrhPassword")?.toString()
            }
        }
        maven {
            name = "sonatypeSnapshots"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = findProperty("ossrhUsername")?.toString()
                password = findProperty("ossrhPassword")?.toString()
            }
        }
    }
}

signing {
    sign(publishing.publications["spigradleAnnotations"])
}

tasks.register("setVersion", VersionTask::class.java)

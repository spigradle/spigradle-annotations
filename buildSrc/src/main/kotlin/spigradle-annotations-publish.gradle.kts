import kr.entree.spigradle.annotations.build.VersionTask

plugins {
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        create("spigradleAnnotations", MavenPublication::class) {
            from(components["java"])
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

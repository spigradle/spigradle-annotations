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
}

signing {
    sign(publishing.publications["spigradleAnnotations"])
}

tasks.register("setVersion", VersionTask::class.java)
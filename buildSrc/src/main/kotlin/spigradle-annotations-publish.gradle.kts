import kr.entree.spigradle.annotations.build.VersionTask
import java.util.*

plugins {
    id("com.jfrog.bintray")
    id("maven-publish")
}

publishing {
    publications {
        create("spigradleAnnotations", MavenPublication::class) {
            from(components["java"])
        }
    }
}

bintray {
    user = findProperty("bintray.publish.user")?.toString()
    key = findProperty("bintray.publish.key")?.toString()
    setPublications("spigradleAnnotations")
    publish = true
    pkg.apply {
        repo = "Spigradle"
        name = project.name
        desc = project.description
        websiteUrl = "https://github.com/spigradle/spigradle-annotations"
        githubRepo = "https://github.com/spigradle/spigradle-annotations"
        issueTrackerUrl = "https://github.com/spigradle/spigradle-annotations/issues"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/spigradle/spigradle-annotations.git"
    }
    project.afterEvaluate {
        pkg.version.apply {
            name = project.version.toString()
            released = Date().toString()
            vcsTag = project.version.toString()
        }
    }
}

tasks.register("setVersion", VersionTask::class.java)
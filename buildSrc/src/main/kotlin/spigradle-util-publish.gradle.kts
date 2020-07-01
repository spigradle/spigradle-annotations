import kr.entree.spigradle.util.build.VersionTask
import java.util.*

plugins {
    id("com.jfrog.bintray")
    id("maven-publish")
}

publishing {
    publications {
        create("spigradleUtil", MavenPublication::class) {
            from(components["java"])
        }
    }
}

bintray {
    user = findProperty("bintray.publish.user")?.toString()
    key = findProperty("bintray.publish.key")?.toString()
    setPublications("spigradleUtil")
    publish = true
    pkg.apply {
        repo = "Spigradle"
        name = project.name
        desc = project.description
        websiteUrl = "https://github.com/EntryPointKR/spigradle-util"
        githubRepo = "https://github.com/EntryPointKR/spigradle-util"
        issueTrackerUrl = "https://github.com/EntryPointKR/spigradle-util/issues"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/EntryPointKR/spigradle-util.git"
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
@file:Suppress("UnstableApiUsage")

import kr.entree.spigradle.util.build.VersionTask

plugins {
    java
    `spigradle-util-publish`
}

group = "kr.entree"
version = VersionTask.readVersion(project)

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}
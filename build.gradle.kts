@file:Suppress("UnstableApiUsage")

plugins {
    java
    `spigradle-util-publish`
}

group = "kr.entree"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}
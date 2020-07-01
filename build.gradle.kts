@file:Suppress("UnstableApiUsage")

plugins {
    java
    `spigradle-publish`
}

group = "kr.entree"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}
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

val annotationProcessorCompile: Configuration by configurations.creating

dependencies {
    annotationProcessorCompile("org.projectlombok:lombok:1.18.12")
    annotationProcessorCompile("com.google.auto.service:auto-service:1.0-rc7")
}

configurations {
    listOf(annotationProcessor, compileOnly).forEach { it.get().extendsFrom(annotationProcessorCompile) }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
@file:Suppress("UnstableApiUsage")

import kr.entree.spigradle.annotations.build.VersionTask

plugins {
    kotlin("jvm") version "1.5.31"
    `spigradle-annotations-publish`
}

group = "kr.entree"
version = VersionTask.readVersion(project)

repositories {
    jcenter()
}

val annotationProcessorCompile: Configuration by configurations.creating

dependencies {
    annotationProcessorCompile("org.projectlombok:lombok:1.18.12")
    annotationProcessorCompile("com.google.auto.service:auto-service:1.0-rc7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(gradleTestKit())
}

configurations {
    listOf(annotationProcessor, compileOnly).forEach { it.get().extendsFrom(annotationProcessorCompile) }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    test {
        useJUnitPlatform()
        systemProperty("spigradle.annotations.jar",
                jar.get().archiveFile.get().asFile.absolutePath.replace("\\", "/"))
        dependsOn(assemble)
    }
}
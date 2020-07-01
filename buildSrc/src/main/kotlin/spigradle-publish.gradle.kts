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
plugins {
    java
    `java-library`
    `maven-publish`
    id("net.kyori.indra") version "4.0.0"
    id("net.kyori.indra.licenser.spotless") version "4.0.0"
    id("net.kyori.indra.checkstyle") version "4.0.0"
}

group = "eu.mikart.adventure"
version = "1.0.2"
description = "Adventure platform implementation for Hytale"

repositories {
    mavenCentral()
    maven("https://maven.hytale.com/release")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api("net.kyori:adventure-platform-api:4.4.2-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-gson:4.21.0")
    api("net.kyori:adventure-text-serializer-ansi:4.21.0")
    api("net.kyori:adventure-platform-facet:4.4.2-SNAPSHOT")
    compileOnly("com.hypixel.hytale:Server:2026.01.28-87d03be09")
    checkstyle("ca.stellardrift:stylecheck:0.2.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

indra {
    javaVersions {
        target(21)
        minimumToolchain(21)
        testWith(21)
    }

    mitLicense()
    github("ArikSquad", "adventure-platform-hytale")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "adventure-platform-hytale"
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        maven("https://repo.codemc.io/repository/ArikSquad/") {
            val mavenUsername = System.getenv("JENKINS_USERNAME")
                ?: findProperty("mavenUsername") as String?
            val mavenPassword = System.getenv("JENKINS_PASSWORD")
                ?: findProperty("mavenPassword") as String?

            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

plugins {
    java
    `java-library`
    `maven-publish`
    id("net.kyori.indra") version "4.0.0"
    id("net.kyori.indra.licenser.spotless") version "4.0.0"
    id("net.kyori.indra.checkstyle") version "4.0.0"
}

group = "eu.mikart.adventure"
version = "1.0.3"
description = "Adventure platform implementation for Hytale"

repositories {
    mavenCentral()
    maven("https://maven.hytale.com/release")
}

dependencies {
    api("net.kyori:adventure-platform-api:4.4.1")
    api("net.kyori:adventure-text-serializer-gson:4.26.1")
    api("net.kyori:adventure-text-serializer-ansi:4.26.1")
    api("net.kyori:adventure-platform-facet:4.4.1")
    compileOnly("com.hypixel.hytale:Server:2026.03.26-89796e57b")
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
        val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
        val repoUrl = if (isSnapshot) {
            "https://repo.codemc.io/repository/ArikSquad/"
        } else {
            "https://repo.codemc.io/repository/ArikSquad/"
        }

        maven(repoUrl) {
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

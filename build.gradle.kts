plugins {
    java
    `java-library`
    `maven-publish`
    id("net.kyori.indra") version "4.0.0"
    id("net.kyori.indra.licenser.spotless") version "4.0.0"
    id("net.kyori.indra.checkstyle") version "4.0.0"
    id("net.kyori.indra.crossdoc") version "4.0.0"
}

group = "net.kyori"
version = "1.0.0-SNAPSHOT"
description = "Adventure platform implementation for Hytale"

val adventure = "4.21.0"

repositories {
    mavenCentral()
    maven("https://maven.hytale.com/pre-release")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("net.kyori:adventure-platform-api:4.4.2-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-gson:${adventure}")
    api("net.kyori:adventure-text-serializer-ansi:${adventure}")
    implementation("net.kyori:adventure-platform-facet:4.4.2-SNAPSHOT")
    compileOnly("com.hypixel.hytale:Server:2026.01.23-d5ecebca9")
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
        testWith(8, 11, 17)
    }

    mitLicense()

    github("ArikSquad", "adventure-platform-hytale")
}


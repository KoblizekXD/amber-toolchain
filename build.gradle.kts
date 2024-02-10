import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "lol.koblizek"
version = "0.0.1"

repositories {
    mavenCentral()
}

val shadowImplement by configurations.creating {
}

configurations.compileOnly.configure {
    extendsFrom(shadowImplement)
}

tasks.withType<ShadowJar> {
    archiveClassifier = null
    configurations = listOf(shadowImplement)
}

dependencies {
    implementation(gradleApi())
    shadowImplement(project(":amber-platform-util"))
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create("amber-toolchain") {
            id = "lol.koblizek.amber-toolchain"
            displayName = "Amber Toolchain"
            description = "Gradle plugin for Amber API development"
            implementationClass = "lol.koblizek.amber.platform.gradle.AmberToolchainPlugin"
            tags = listOf("minecraft", "modding", "decompilation", "deobfuscation", "amber", "amber-toolchain")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = groupId
            artifactId = "amber-toolchain"
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
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
    maven("https://maven.neoforged.net/releases")
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
    shadowImplement("org.ow2.asm:asm:9.6")
    shadowImplement("org.ow2.asm:asm-tree:9.6")
    shadowImplement("org.ow2.asm:asm-commons:9.6")
    shadowImplement("net.fabricmc:mapping-io:0.5.1")
    shadowImplement("net.neoforged:AutoRenamingTool:1.0.14")
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
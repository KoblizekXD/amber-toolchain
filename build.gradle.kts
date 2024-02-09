plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
}

group = "lol.koblizek"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
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
            artifactId = rootProject.name
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
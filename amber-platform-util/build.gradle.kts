plugins {
    `java-library`
}

group = "lol.koblizek"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}
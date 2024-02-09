package lol.koblizek.amber.platform.gradle.extensions

import org.gradle.api.provider.Property

interface MinecraftExtension {
    val minecraft: Property<String>

    fun minecraft(version: String) {
        minecraft.set(version)
    }
}
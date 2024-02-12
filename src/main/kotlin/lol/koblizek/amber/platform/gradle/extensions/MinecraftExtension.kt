package lol.koblizek.amber.platform.gradle.extensions

import lol.koblizek.amber.platform.GameVersion
import lol.koblizek.amber.platform.MappingProvider
import lol.koblizek.amber.platform.VersionData
import org.gradle.api.Project
import org.gradle.api.provider.Property

abstract class MinecraftExtension(private val project: Project) {

    abstract fun getVersionData(): Property<VersionData>

    /**
     * Specifies Minecraft version for Amber toolchain to be using
     */
    fun minecraft(version: String) {
        getVersionData().set(VersionData(GameVersion.getProviding(version), MappingProvider.OFFICIAL))
    }

    fun mappings(version: String) {
        if (getVersionData().isPresent) {
            getVersionData().set(VersionData(getVersionData().get().version, MappingProvider.get(version)))
        } else {
            println("Can't set mappings, version was not provided")
        }
    }

    fun game() {
        // Add the game dependency itself here
        // project.dependencies.add("compileOnly", "")
    }

    companion object {
        val Project.minecraftExtension: MinecraftExtension
            get() = extensions.getByType(MinecraftExtension::class.java)
    }
}
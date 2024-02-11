package lol.koblizek.amber.platform.gradle.extensions

import groovy.lang.Closure
import lol.koblizek.amber.platform.GameVersion
import lol.koblizek.amber.platform.MappingProvider
import lol.koblizek.amber.platform.VersionData
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Property

abstract class MinecraftExtension(private val project: Project) {

    abstract fun versionData(): Property<VersionData>

    /**
     * Specifies Minecraft version for Amber toolchain to be using
     */
    fun minecraft(version: String) {
        versionData().set(VersionData(GameVersion.getProviding(version), MappingProvider.OFFICIAL))
    }

    fun mappings(version: String) {
        if (versionData().isPresent) {
            versionData().set(VersionData(versionData().get().version, MappingProvider.get(version)))
        } else {
            println("Can't set mappings, version was not provided")
        }
    }

    fun game() {
        project.dependencies.add("compileOnly", "")
    }

    internal val Project.minecraftExtension: MinecraftExtension
        get() = extensions.getByType(MinecraftExtension::class.java)
}
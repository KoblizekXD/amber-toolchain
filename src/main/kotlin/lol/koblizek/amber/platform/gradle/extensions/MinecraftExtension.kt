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
     *
     * @throws IllegalStateException if invalid version was provided
     */
    fun minecraft(version: String) {
        getVersionData().set(VersionData(GameVersion.getProviding(version), MappingProvider.OFFICIAL))
    }

    /**
     * Specifies mappings for Amber toolchain to be using
     * @throws IllegalStateException if version was not previously provided
     */
    fun mappings(version: String) {
        if (getVersionData().isPresent) {
            getVersionData().set(VersionData(getVersionData().get().version, MappingProvider.get(version)))
        } else {
            throw IllegalStateException("Can't set mappings, version was not provided")
        }
    }

    companion object {
        val Project.minecraftExtension: MinecraftExtension
            get() = extensions.getByType(MinecraftExtension::class.java)
    }
}
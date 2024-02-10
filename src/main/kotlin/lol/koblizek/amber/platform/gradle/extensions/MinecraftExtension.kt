package lol.koblizek.amber.platform.gradle.extensions

import lol.koblizek.amber.platform.GameVersion
import lol.koblizek.amber.platform.MappingProvider
import lol.koblizek.amber.platform.VersionData
import org.gradle.api.provider.Property

abstract class MinecraftExtension {
    abstract val versionData: Property<VersionData>

    fun version(version: String, mappings: String) {
        versionData.set(VersionData(GameVersion.getProviding(version), MappingProvider.get(mappings)))
    }
}
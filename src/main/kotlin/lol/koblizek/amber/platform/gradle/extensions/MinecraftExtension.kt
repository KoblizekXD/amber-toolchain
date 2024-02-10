package lol.koblizek.amber.platform.gradle.extensions

import lol.koblizek.amber.platform.MappingProvider
import lol.koblizek.amber.platform.VersionData
import org.gradle.api.provider.Property

interface MinecraftExtension {
    val versionData: Property<VersionData>

    fun minecraft(version: String, mappings: String) {
        versionData.set(VersionData(version, MappingProvider.get(mappings)))
    }
}
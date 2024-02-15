package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.gradle.util.Described
import lol.koblizek.amber.platform.gradle.util.Preconditions.Companion.preconditions
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@Described("Collects data required to decompile Minecraft")
abstract class CollectRequiredData : AmberTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputMappings: RegularFileProperty

    @TaskAction
    fun onExecute() {
        if (project.preconditions.isVersionSet()) {
            val versionData = project.minecraftExtension.getVersionData().get()
            val mcData = versionData.mappings.versionProvider.getMinecraftData(versionData.version)
            // TODO: Implement server as well!
            outputFile.set(download(mcData.clientJarUrl))
            outputMappings.set(download(mcData.clientMappingsUrl))
        } else {
            throw IllegalStateException("Versions are not set, cannot determine what to download!")
        }
    }
}
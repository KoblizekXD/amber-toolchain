package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.Environment
import lol.koblizek.amber.platform.GameVersion
import lol.koblizek.amber.platform.MappingProvider
import lol.koblizek.amber.platform.fabric.MinecraftJarMerger
import lol.koblizek.amber.platform.format.OfficialVendorProvider
import lol.koblizek.amber.platform.format.OfficialVendorProvider.MojangGameDataProvider
import lol.koblizek.amber.platform.format.OfficialVendorProvider.OfficialMappingProcessor
import lol.koblizek.amber.platform.gradle.extensions.AmberExtension.Companion.amberExtension
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.gradle.util.Described
import lol.koblizek.amber.platform.gradle.util.MappingUtil
import lol.koblizek.amber.platform.gradle.util.Preconditions.Companion.preconditions
import net.fabricmc.mappingio.tree.MemoryMappingTree
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@Described("Collects data required to decompile Minecraft")
abstract class CollectRequiredData : AmberTask() {

    @OutputFile
    abstract fun getOutputFile(): RegularFileProperty

    @OutputFile
    abstract fun getOutputMappings(): RegularFileProperty

    init {
        getOutputFile().set(temporaryDir.resolve("minecraft.jar"))
        getOutputFile().finalizeValue()
        getOutputMappings().set(temporaryDir.resolve("mappings.txt"))
        getOutputMappings().finalizeValue()
    }

    @TaskAction
    fun onExecute() {
        if (project.preconditions.isVersionSet() && project.preconditions.envNotNull()) {
            val versionData = project.minecraftExtension.getVersionData().get()
            println("Found: ${versionData.version}-${versionData.mappings}")
            val mcData = versionData.mappings.versionProvider.getMinecraftData(versionData.version)
            when (project.amberExtension.getEnvironment().get()) {
                Environment.CLIENT -> {
                    println("Selected client environment")
                    download(mcData.clientMappingsUrl).renameTo(getOutputMappings().get().asFile)
                    download(mcData.clientJarUrl).renameTo(getOutputFile().get().asFile)
                }
                Environment.SERVER -> {
                    println("Selected server environment")
                    download(mcData.serverMappingsUrl).renameTo(getOutputMappings().get().asFile)
                    download(mcData.serverJarUrl).renameTo(getOutputFile().get().asFile)
                }
                Environment.BOTH -> {
                    val client = download(mcData.clientJarUrl)
                    val server = download(mcData.serverJarUrl)
                    val cM = download(mcData.clientMappingsUrl)
                    val sM = download(mcData.serverMappingsUrl)
                    val common = getOutputMappings().get().asFile
                    val processor = mcData.getMappingProcessor<MojangGameDataProvider>(cM, sM)
                    processor.merge(common.toPath())
                    if (mcData.version.ordinal < GameVersion.V1_17.ordinal) {
                        println("[Fabric] Merging client and server jars...")
                        MinecraftJarMerger(client, server, temporaryDir.resolve("minecraft.jar")).use {
                            it.merge()
                        }
                        println("[Fabric] Merging done!")
                    } else client.copyTo(getOutputFile().get().asFile, true)
                }
                null -> throw IllegalStateException("Environment is not set, cannot determine what to download!")
            }
            getCache().addFile(getOutputFile().get().asFile)
            getCache().addFile(getOutputMappings().get().asFile)
        } else {
            throw IllegalStateException("Versions are not set, cannot determine what to download!")
        }
    }
}
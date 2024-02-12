package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.tasks.TaskAction

@Described("Returns data for current version provided by manifest")
abstract class GetCurrentVersionData : AmberTask() {

    @TaskAction
    fun onExecute() {
        project.minecraftExtension.getVersionData().orNull?.let {
            println(it.mappings.versionProvider.getMinecraftData(it.version).asString)
        } ?: {
            throw IllegalStateException("No version was specified")
        }
    }
}
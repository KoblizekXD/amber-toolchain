package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.format.OfficialVendorProvider
import lol.koblizek.amber.platform.gradle.println
import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.tasks.TaskAction

@Described("Provides user with all available versions")
abstract class GetAllVersionsTask : AmberTask() {
    @TaskAction
    fun onExecute() {
        println("Here is a list of all available versions:")
        OfficialVendorProvider().allVersions.println()
    }
}
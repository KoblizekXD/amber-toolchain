package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@Described("Remaps a jar using a given mapping file")
abstract class RemapJar : AmberTask() {

    @InputFile
    abstract fun getInputJar(): RegularFileProperty
    @InputFile
    abstract fun getMappings(): RegularFileProperty

    @OutputFile
    abstract fun getOutputJar(): RegularFileProperty

    init {
        getOutputJar().set(temporaryDir.resolve("remapped.jar"))
        getOutputJar().finalizeValue()
    }

    @TaskAction
    fun onExecute() {

    }
}
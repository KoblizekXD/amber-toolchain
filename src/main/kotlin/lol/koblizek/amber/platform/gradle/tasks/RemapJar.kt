package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.util.Described
import net.minecraftforge.fart.api.IdentifierFixerConfig
import net.minecraftforge.fart.api.Renamer
import net.minecraftforge.fart.api.SourceFixerConfig
import net.minecraftforge.fart.api.Transformer
import net.minecraftforge.srgutils.IMappingFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@Described("Remaps a jar using a given mapping file")
abstract class RemapJar : AmberTask() {

    @InputFile
    abstract fun getInputJar(): RegularFileProperty
    @InputFile
    abstract fun getMappings(): RegularFileProperty
    @Input
    abstract fun getIsReverse(): Property<Boolean>
    @Input
    abstract fun getPostRemapAction(): Property<PostRemapAction>

    @OutputFile
    abstract fun getOutputJar(): RegularFileProperty

    init {
        getInputJar().set(temporaryDir.resolve("input.jar"))
        getMappings().set(temporaryDir.resolve("mappings.txt"))
        getIsReverse().convention(false)
        getPostRemapAction().convention(PostRemapAction.CACHE)

        getOutputJar().set(temporaryDir.resolve("remapped.jar"))
        getOutputJar().finalizeValue()
    }

    @TaskAction
    fun onExecute() {
        var mapping = IMappingFile.load(getMappings().get().asFile);
        if (getIsReverse().get())
            mapping = mapping.reverse()
        val renamerBuilder = Renamer.builder()
            .withJvmClasspath()
            .add(Transformer.identifierFixerFactory(IdentifierFixerConfig.ALL))
            .add(Transformer.sourceFixerFactory(SourceFixerConfig.JAVA))
            .add(Transformer.renamerFactory(mapping, true))
            .add(Transformer.recordFixerFactory())
            .add(Transformer.parameterAnnotationFixerFactory())

        project.configurations.getByName("minecraftLibrary")
            .resolve().forEach {
                renamerBuilder.lib(it)
            }

        val renamer = renamerBuilder.build()
        renamer.run(getInputJar().asFile.get(), getOutputJar().asFile.get())

        if (getPostRemapAction().get() == PostRemapAction.CACHE) {
            val cache = getCache()
            cache.addFile(getOutputJar().asFile.get())
        }
    }

    companion object {
        enum class PostRemapAction {
            CACHE, NONE
        }
    }
}
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
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode

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
        // We know there's only one CollectRequiredData task, so no need to check for others!
        dependsOn(project.tasks.getByName("collectRequiredData"))
        project.tasks.withType(CollectRequiredData::class.java).first()?.let {
            getInputJar().set(it.getOutputFile())
            getMappings().set(it.getOutputMappings())
        }
        getIsReverse().convention(false)
        getPostRemapAction().convention(PostRemapAction.CACHE)

        getOutputJar().set(temporaryDir.resolve("remapped.jar"))
        getOutputJar().finalizeValue()
    }

    @TaskAction
    fun onExecute() {
        var mapping = IMappingFile.load(getMappings().get().asFile)
        if (!getIsReverse().get())
            mapping = mapping.reverse()
        val renamerBuilder = Renamer.builder()
            .withJvmClasspath()
            .add(Transformer.identifierFixerFactory(IdentifierFixerConfig.ALL))
            .add(Transformer.sourceFixerFactory(SourceFixerConfig.JAVA))
            .add(Transformer.renamerFactory(mapping, true))
            .add(Transformer.recordFixerFactory())
            .add(Transformer.parameterAnnotationFixerFactory())
            .add(LocalVariableFixer())

        project.configurations.getByName("minecraftLibrary")
            .resolve().forEach {
                renamerBuilder.lib(it)
            }

        println("[ART] Remapping jar...")
        renamerBuilder.build().use {
            it.run(getInputJar().asFile.get(), getOutputJar().asFile.get())
        }
        println("[ART] Remapping done")

        if (getPostRemapAction().get() == PostRemapAction.CACHE) {
            val cache = getCache()
            cache.addFile(getOutputJar().asFile.get())
        }
    }

    companion object {
        enum class PostRemapAction {
            CACHE, NONE
        }

        class LocalVariableFixer : Transformer {
            override fun process(entry: Transformer.ClassEntry): Transformer.ClassEntry {
                val reader = ClassReader(entry.data)
                val writer = ClassWriter(0)
                val node = ClassNode()
                reader.accept(node, 0)
                node.methods.forEach {
                    it.accept(MethodProcessor())
                }
                node.accept(writer)
                return Transformer.ClassEntry.create(entry.name, entry.time, writer.toByteArray())
            }

            class MethodProcessor : MethodVisitor(Opcodes.ASM9) {
                override fun visitLocalVariable(
                    name: String?,
                    descriptor: String?,
                    signature: String?,
                    start: Label?,
                    end: Label?,
                    index: Int
                ) {
                    if (name != "this")
                        super.visitLocalVariable("p${Type.getType(descriptor).className}${index}", descriptor, signature, start, end, index)
                    else super.visitLocalVariable(name, descriptor, signature, start, end, index)
                }
            }
        }
    }
}
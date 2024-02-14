package lol.koblizek.amber.platform.gradle.extensions

import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.util.Os
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import java.io.File

abstract class AmberExtension(private val project: Project) : ExtensionAware {

    abstract class SourcesExtension {
        abstract fun getMinecraft(): Property<String>
        abstract fun getPatchSupport(): Property<String>
        abstract fun getPatches(): Property<String>

        fun patches(dir: File) {
            getPatches().set(dir.path)
        }

        fun patchSupport(dir: File) {
            getPatchSupport().set(dir.path)
        }

        fun minecraftSource(dir: File) {
            getMinecraft().set(dir.path)
        }

        val AmberExtension.sources: SourcesExtension
            get() = extensions.getByType(SourcesExtension::class.java)
    }

    init {
        extensions.create("sources", SourcesExtension::class.java)
    }

    abstract fun getEnableDevelopment(): Property<Boolean>

    fun enableDevelopment() {
        getEnableDevelopment().set(true)
    }

    fun minecraft(): Dependency {
        return project.dependencies.create("com.mojang:minecraft:")
    }

    fun libraries(): List<Dependency> {
        val deps = project.minecraftExtension.getVersionData().orNull
        return deps?.mappings?.versionProvider?.getMinecraftData(deps.version)?.libraries?.filter {
            !it.isNative
        }?.map {
                project.dependencies.create(it.artifact)
        } ?: listOf()
    }

    fun runtimeLibraries(): List<Dependency> {
        val deps = project.minecraftExtension.getVersionData().orNull
        return deps?.mappings?.versionProvider?.getMinecraftData(deps.version)?.libraries?.filter {
            it.isNative && it.matchesOs(Os.getOS())
        }?.map {
            project.dependencies.create(it.artifact)
        } ?: listOf()
    }

    fun applyGameLibraries() {
        project.dependencies.add("implementation", minecraft())
        libraries().forEach {
            project.dependencies.add("implementation", it)
        }
        runtimeLibraries().forEach {
            project.dependencies.add("implementation", it)
        }
    }

    companion object {
        val Project.amberExtension: MinecraftExtension
            get() = extensions.getByType(MinecraftExtension::class.java)
    }
}
package lol.koblizek.amber.platform.gradle.extensions

import lol.koblizek.amber.platform.Environment
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.util.Os
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import java.io.File

abstract class AmberExtension(private val project: Project) : ExtensionAware {

    abstract class SourcesExtension {

        /**
         * Specifies directory where Minecraft source code is located,
         * this source can be freely edited and what the patches are based on.
         */
        abstract fun getMinecraft(): Property<String>

        /**
         * Specifies directory where duplicate Minecraft source code is located,
         * this source is used to create `diff`s for patches, it's generally not recommended to edit
         */
        abstract fun getPatchSupport(): Property<String>

        /**
         * Specifies directory where patches for Minecraft source code are located,
         * in the most cases it's in the `/patches` directory in the root of the project
         */
        abstract fun getPatches(): Property<String>

        /**
         * Specifies directory where patches for Minecraft source code are located,
         * in the most cases it's in the `/patches` directory in the root of the project
         */
        fun patches(dir: File) {
            getPatches().set(dir.path)
        }

        /**
         * Specifies directory where duplicate Minecraft source code is located,
         * this source is used to create `diff`s for patches, it's generally not recommended to edit
         */
        fun patchSupport(dir: File) {
            getPatchSupport().set(dir.path)
        }

        /**
         * Specifies directory where Minecraft source code is located,
         * this source can be freely edited and what the patches are based on.
         */
        fun minecraftSource(dir: File) {
            getMinecraft().set(dir.path)
        }

        companion object {
            val AmberExtension.sources: SourcesExtension
                get() = extensions.getByType(SourcesExtension::class.java)
        }
    }

    init {
        getEnvironment().convention(Environment.BOTH)
        extensions.create("sources", SourcesExtension::class.java)
    }

    abstract fun getEnableDevelopment(): Property<Boolean>
    abstract fun getEnvironment(): Property<Environment>

    /**
     * Enables development mode for Amber toolchain, which will allow a
     * user to develop and edit Minecraft's source code
     */
    fun enableDevelopment() {
        getEnableDevelopment().set(true)
    }

    fun useEnvironment(env: Environment) {
        getEnvironment().set(env)
    }

    /**
     * Returns Minecraft as dependency, this is used only when development mode is disabled
     * and null will be returned when development mode is enabled.
     * The recommended configuration for this dependency is `compileOnly`, as
     * `AmberLoader` will load the Minecraft dependency at runtime
     *
     * @return Minecraft as dependency or null
     */
    fun minecraft(): Dependency {
        // TODO: Make return correctly
        return project.dependencies.create("com.mojang:minecraft:")
    }

    /**
     * Returns list of dependencies required for Minecraft to run
     */
    fun libraries(): List<Dependency> {
        val deps = project.minecraftExtension.getVersionData().orNull
        return deps?.mappings?.versionProvider?.getMinecraftData(deps.version)?.libraries?.filter {
            !it.isNative
        }?.map {
                project.dependencies.create(it.artifact)
        } ?: listOf()
    }

    /**
     * Returns list of runtime dependencies required for Minecraft to run,
     * this mostly includes native libraries, it's generally recommended to use `runtimeOnly`
     * configuration for these dependencies
     */
    fun runtimeLibraries(): List<Dependency> {
        val deps = project.minecraftExtension.getVersionData().orNull
        return deps?.mappings?.versionProvider?.getMinecraftData(deps.version)?.libraries?.filter {
            it.isNative && it.matchesOs(Os.getOS())
        }?.map {
            project.dependencies.create(it.artifact)
        } ?: listOf()
    }

    /**
     * Applies game libraries to the project with recommended configurations
     */
    fun applyGameLibraries() {
        project.dependencies.add("compileOnly", minecraft())
        libraries().forEach {
            project.dependencies.add("implementation", it)
        }
        runtimeLibraries().forEach {
            project.dependencies.add("implementation", it)
        }
    }

    companion object {
        val Project.amberExtension: AmberExtension
            get() = extensions.getByType(AmberExtension::class.java)
    }
}
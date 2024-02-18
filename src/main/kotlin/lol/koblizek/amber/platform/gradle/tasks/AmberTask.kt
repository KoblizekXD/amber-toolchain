package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.VersionData
import lol.koblizek.amber.platform.gradle.download
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import lol.koblizek.amber.platform.gradle.util.Described
import lol.koblizek.amber.platform.gradle.util.cache.CacheData
import org.gradle.api.DefaultTask
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation


abstract class AmberTask : DefaultTask() {
    init {
        group = "amber-toolchain"
        if (this::class.hasAnnotation<Described>())
            description = this::class.findAnnotation<Described>()?.value
    }

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor


    fun download(file: String): File {
        val fileName = file.substring( file.lastIndexOf('/')+1, file.length )
        return project.download(file, this.temporaryDir.resolve(fileName).path)
    }

    /**
     * Get the cache for the current version, set by the user,
     * may produce unexpected results if the version is not set.
     * Make sure to use precondition before using.
     *
     * @return CacheData
     */
    fun getCache(): CacheData {
        project.minecraftExtension.getVersionData().get().let {
            return CacheData(it, getCacheDirectory().resolve("amber-toolchain/versioning/${it.version}-${it.mappings}/"))
        }
    }

    fun hasCache(version: VersionData): Boolean {
        return getCacheDirectory().resolve("amber-toolchain/versioning/${version.version}-${version.mappings}/").exists()
    }

    fun getCache(version: VersionData): CacheData {
        return CacheData(version, getCacheDirectory().resolve("amber-toolchain/versioning/${version.version}-${version.mappings}/"))
    }

    fun getCacheDirectory(): File {
        return project.gradle.gradleUserHomeDir.resolve("caches/")
    }
}
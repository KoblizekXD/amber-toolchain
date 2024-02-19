package lol.koblizek.amber.platform.gradle.util.cache

import lol.koblizek.amber.platform.VersionData
import java.io.File

class CacheData(val versionData: VersionData, val cacheDir: File) {
    fun exists(): Boolean {
        return cacheDir.exists() && cacheDir.isDirectory
    }

    fun create() {
        cacheDir.mkdirs()
    }

    fun addFile(file: File) {
        if (!exists()) create()
        file.copyTo(cacheDir.resolve(file.name), true)
    }

    fun has(file: String): Boolean {
        return cacheDir.resolve(file).exists()
    }

    fun getDataFile(): File {
        val d = cacheDir.resolve("data.properties")
        if (!d.exists()) d.createNewFile()
        return d
    }

    fun getFile(name: String): File {
        return cacheDir.resolve(name)
    }

    companion object {
        fun File.appendLine(line: String) {
            appendText("$line\n")
        }

        fun File.writeLine(line: String) {
            writeText("$line\n")
        }
    }
}
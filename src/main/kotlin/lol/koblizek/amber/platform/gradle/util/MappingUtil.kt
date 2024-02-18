package lol.koblizek.amber.platform.gradle.util

import net.fabricmc.mappingio.format.proguard.ProGuardFileReader
import net.fabricmc.mappingio.format.proguard.ProGuardFileWriter
import net.fabricmc.mappingio.tree.MemoryMappingTree
import java.nio.file.Files
import java.nio.file.Path


class MappingUtil {
    companion object {

        /**
         * Merges two official mappings(proguard) into one
         * @param input1 first input mappings(client)
         * @param input2 second input mappings(server)
         * @param output output path to generated mappings
         */
        @Deprecated("Deprecated in favor of platform-util class")
        fun mergeOfficial(input1: Path, input2: Path, output: Path) {
            print("Merging official mappings...")
            val tree = MemoryMappingTree()
            Files.newBufferedWriter(output).use { buffer ->
                ProGuardFileReader.read(Files.newBufferedReader(input1), tree)
                ProGuardFileReader.read(Files.newBufferedReader(input2), tree)
                tree.accept(ProGuardFileWriter(buffer))
            }
        }
    }
}
package lol.koblizek.amber.platform.gradle.util

import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.MappingWriter
import net.fabricmc.mappingio.format.MappingFormat
import net.fabricmc.mappingio.tree.MemoryMappingTree
import java.nio.file.Path

class MappingUtil {
    companion object {
        fun mergeOfficial(input1: Path, input2: Path, output: Path) {
            print("Merging official mappings...")
            val tree = MemoryMappingTree()
            MappingReader.read(input1, MappingFormat.PROGUARD_FILE, tree)
            MappingReader.read(input2, MappingFormat.PROGUARD_FILE, tree)
            tree.accept(MappingWriter.create(output, MappingFormat.PROGUARD_FILE))
        }
    }
}
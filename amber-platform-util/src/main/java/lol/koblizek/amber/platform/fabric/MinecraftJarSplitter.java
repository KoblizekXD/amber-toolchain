package lol.koblizek.amber.platform.fabric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;

public class MinecraftJarSplitter implements AutoCloseable {
    private final Path clientInputJar;
    private final Path serverInputJar;

    private EntryData entryData;
    private Set<String> sharedEntries = new HashSet<>();
    private Set<String> forcedClientEntries = new HashSet<>();

    public MinecraftJarSplitter(Path clientInputJar, Path serverInputJar) {
        this.clientInputJar = Objects.requireNonNull(clientInputJar);
        this.serverInputJar = Objects.requireNonNull(serverInputJar);
    }

    public void split(Path clientOnlyOutputJar, Path commonOutputJar) throws IOException {
        Objects.requireNonNull(clientOnlyOutputJar);
        Objects.requireNonNull(commonOutputJar);

        if (entryData == null) {
            entryData = new EntryData(getJarEntries(clientInputJar), getJarEntries(serverInputJar));
        }

        // Not something we expect, will require 3 jars, server, client and common.
        assert entryData.serverOnlyEntries.isEmpty();

        copyEntriesToJar(entryData.commonEntries, serverInputJar, commonOutputJar, "common");
        copyEntriesToJar(entryData.clientOnlyEntries, clientInputJar, clientOnlyOutputJar, "client");
    }

    public void sharedEntry(String path) {
        this.sharedEntries.add(path);
    }

    public void forcedClientEntry(String path) {
        this.forcedClientEntries.add(path);
    }

    private Set<String> getJarEntries(Path input) throws IOException {
        Set<String> entries = new HashSet<>();

        try (FileSystemUtil.Delegate fs = FileSystemUtil.getJarFileSystem(input);
             Stream<Path> walk = Files.walk(fs.get().getPath("/"))) {
            Iterator<Path> iterator = walk.iterator();

            while (iterator.hasNext()) {
                Path fsPath = iterator.next();

                if (!Files.isRegularFile(fsPath)) {
                    continue;
                }

                String entryPath = fs.get().getPath("/").relativize(fsPath).toString();

                if (entryPath.startsWith("META-INF/")) {
                    continue;
                }

                entries.add(entryPath);
            }
        }

        return entries;
    }

    private void copyEntriesToJar(Set<String> entries, Path inputJar, Path outputJar, String env) throws IOException {
        Files.deleteIfExists(outputJar);

        try (FileSystemUtil.Delegate inputFs = FileSystemUtil.getJarFileSystem(inputJar);
             FileSystemUtil.Delegate outputFs = FileSystemUtil.getJarFileSystem(outputJar, true)) {
            for (String entry : entries) {
                Path inputPath = inputFs.get().getPath(entry);
                Path outputPath = outputFs.get().getPath(entry);

                assert Files.isRegularFile(inputPath);

                Path outputPathParent = outputPath.getParent();

                if (outputPathParent != null) {
                    Files.createDirectories(outputPathParent);
                }

                Files.copy(inputPath, outputPath, StandardCopyOption.COPY_ATTRIBUTES);
            }

            writeManifest(outputFs, env);
        }
    }

    private void writeManifest(FileSystemUtil.Delegate outputFs, String env) throws IOException {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue(FabricASM.Manifest.SPLIT_ENV_NAME, env);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write(out);
        Files.createDirectories(outputFs.get().getPath("META-INF"));
        Files.write(outputFs.get().getPath(FabricASM.Manifest.PATH), out.toByteArray());
    }

    @Override
    public void close() throws Exception {
    }

    private final class EntryData {
        private final Set<String> clientEntries;
        private final Set<String> serverEntries;
        private final Set<String> commonEntries;
        private final Set<String> clientOnlyEntries;
        private final Set<String> serverOnlyEntries;

        private EntryData(Set<String> clientEntries, Set<String> serverEntries) {
            this.clientEntries = clientEntries;
            this.serverEntries = serverEntries;

            this.commonEntries = new HashSet<>(clientEntries);
            this.commonEntries.retainAll(serverEntries);
            this.commonEntries.addAll(sharedEntries);
            this.commonEntries.removeAll(forcedClientEntries);

            this.clientOnlyEntries = new HashSet<>(clientEntries);
            this.clientOnlyEntries.removeAll(serverEntries);
            this.clientOnlyEntries.addAll(sharedEntries);
            this.clientOnlyEntries.addAll(forcedClientEntries);

            this.serverOnlyEntries = new HashSet<>(serverEntries);
            this.serverOnlyEntries.removeAll(clientEntries);
        }
    }
}
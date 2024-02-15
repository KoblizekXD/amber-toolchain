package lol.koblizek.amber.platform.fabric;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class FileSystemUtil {
    public record Delegate(FileSystemReference reference) implements AutoCloseable, Supplier<FileSystem> {
        public Path getPath(String path, String... more) {
            return get().getPath(path, more);
        }

        public byte[] readAllBytes(String path) throws IOException {
            Path fsPath = getPath(path);

            if (Files.exists(fsPath)) {
                return Files.readAllBytes(fsPath);
            } else {
                throw new NoSuchFileException(fsPath.toString());
            }
        }

        public <T> T fromInputStream(IOFunction<InputStream, T> function, String path, String... more) throws IOException {
            try (InputStream inputStream = Files.newInputStream(getPath(path, more))) {
                return function.apply(inputStream);
            }
        }

        public String readString(String path) throws IOException {
            return new String(readAllBytes(path), StandardCharsets.UTF_8);
        }

        @Override
        public void close() throws IOException {
            reference.close();
        }

        @Override
        public FileSystem get() {
            return reference.getFs();
        }

        // TODO cleanup
        public FileSystem fs() {
            return get();
        }
    }

    private FileSystemUtil() {
    }

    public static Delegate getJarFileSystem(File file, boolean create) throws IOException {
        return new Delegate(FileSystemReference.openJar(file.toPath(), create));
    }

    public static Delegate getJarFileSystem(Path path, boolean create) throws IOException {
        return new Delegate(FileSystemReference.openJar(path, create));
    }

    public static Delegate getJarFileSystem(Path path) throws IOException {
        return new Delegate(FileSystemReference.openJar(path));
    }

    public static Delegate getJarFileSystem(URI uri, boolean create) throws IOException {
        return new Delegate(FileSystemReference.open(uri, create));
    }

    @FunctionalInterface
    public interface IOFunction<T, R> {
        R apply(T t) throws IOException;
    }
}
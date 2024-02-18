package lol.koblizek.amber.platform.format;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.koblizek.amber.platform.GameVersion;
import lol.koblizek.amber.platform.MappingProvider;
import lol.koblizek.amber.platform.VersionData;
import net.fabricmc.mappingio.format.proguard.ProGuardFileReader;
import net.fabricmc.mappingio.format.proguard.ProGuardFileWriter;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class OfficialVendorProvider implements VendorSpecificVersionProvider {
    @Override
    public MappingProvider getAsMappingProvider() {
        return MappingProvider.OFFICIAL;
    }

    @Override
    public String getName() {
        return "Mojang/Official";
    }

    @Override
    public GameDataProvider getMinecraftData(GameVersion version) {
        if (!getAllVersions().contains(version)) return null; // Must return null as it's not supported by mappings
        try (var manifestReader = new InputStreamReader(new URI(getVersionManifestUrl()).toURL().openStream())) {
            JsonObject manifest = gson.fromJson(manifestReader, JsonObject.class);
            for (JsonElement versions : manifest.getAsJsonArray("versions")) {
                JsonObject versionObject = versions.getAsJsonObject();
                if (versionObject.get("id").getAsString().equals(version.toString())) {
                    try (var versionReader = new InputStreamReader(new URI(versionObject.get("url").getAsString()).toURL().openStream())) {
                        JsonObject versionData = gson.fromJson(versionReader, JsonObject.class);
                        return new MojangGameDataProvider(versionData, versionObject.get("url").getAsString(), new VersionData(version, getAsMappingProvider()));
                    }
                }
            }
            return null;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MojangGameDataProvider implements GameDataProvider {

        private final transient JsonObject json;
        private final String jsonUrl;
        private final VersionData version;

        MojangGameDataProvider(JsonObject json, String jsonUrl, VersionData version) {
            this.json = json;
            this.jsonUrl = jsonUrl;
            this.version = version;
        }

        @Override
        public String getJsonUrl() {
            return jsonUrl;
        }

        @Override
        public List<Library> getLibraries() {
            List<JsonObject> libraries = new ArrayList<>();
            json.getAsJsonArray("libraries").forEach(e -> libraries.add(e.getAsJsonObject()));
            return libraries.stream().map(object -> new Library(
                    object.getAsJsonObject("downloads").getAsJsonObject("artifact").getAsJsonPrimitive("path").getAsString(),
                    object.getAsJsonPrimitive("name").getAsString(),
                    Library.LibraryAction.getCorresponding(object),
                    object.getAsJsonPrimitive("name").getAsString().contains("natives")
            )).toList();
        }

        @Override
        public String getPathToClientMainClass() {
            return json.getAsJsonPrimitive("mainClass").getAsString();
        }

        @Override
        public int getMinimumJavaVersion() {
            return json.getAsJsonObject("javaVersion").getAsJsonPrimitive("majorVersion")
                    .getAsInt();
        }

        @Override
        public AssetIndex getAssetIndex() {
            return new AssetIndex(json.getAsJsonObject("assetIndex").getAsJsonPrimitive("id").getAsString(),
                    json.getAsJsonObject("assetIndex").getAsJsonPrimitive("url").getAsString());
        }

        @Override
        public String getClientJarUrl() {
            return json.getAsJsonObject("downloads").getAsJsonObject("client").getAsJsonPrimitive("url")
                    .getAsString();
        }

        @Override
        public String getServerJarUrl() {
            return json.getAsJsonObject("downloads").getAsJsonObject("server").getAsJsonPrimitive("url")
                    .getAsString();
        }

        @Override
        public String getClientMappingsUrl() {
            return json.getAsJsonObject("downloads").getAsJsonObject("client_mappings").getAsJsonPrimitive("url")
                    .getAsString();
        }

        @Override
        public String getServerMappingsUrl() {
            return json.getAsJsonObject("downloads").getAsJsonObject("server_mappings").getAsJsonPrimitive("url")
                    .getAsString();
        }

        @Override
        public GameVersion getVersion() {
            return version.version();
        }

        @Override
        public MappingProvider getMappingProvider() {
            return version.mappings();
        }

        @Override
        public List<ArgumentPart> getGameArguments() {
            JsonObject args = json.getAsJsonObject("arguments");
            List<ArgumentPart> list = new ArrayList<>();
            args.getAsJsonArray("game").forEach(e -> list.add(ArgumentPart.create(e)));
            return list;
        }

        @Override
        public List<ArgumentPart> getJvmArguments() {
            JsonObject args = json.getAsJsonObject("arguments");
            List<ArgumentPart> list = new ArrayList<>();
            args.getAsJsonArray("jvm").forEach(e -> list.add(ArgumentPart.create(e)));
            return list;
        }

        @Override
        public <T extends GameDataProvider> MappingProcessor<T> getMappingProcessor(File client, File server) {
            return (MappingProcessor<T>) new OfficialMappingProcessor((OfficialVendorProvider) version.mappings().getVersionProvider(), this, client, server);
        }
    }

    public record OfficialMappingProcessor(OfficialVendorProvider vendorProvider, MojangGameDataProvider gameData, File client, File server) implements MappingProcessor<MojangGameDataProvider> {

        @Override
        public MojangGameDataProvider getVersionProvider() {
            return null;
        }

        @Override
        public File getClientMappings() {
            return client;
        }

        @Override
        public File getServerMappings() {
            return server;
        }

        @Override
        public void merge(Path output) {
            if (gameData.version.version().ordinal() >= GameVersion.V1_17.ordinal()) {
                try {
                    Files.copy(getClientMappings().toPath(), output, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                MemoryMappingTree tree = new MemoryMappingTree();
                try (var buffer = Files.newBufferedWriter(output)) {
                    ProGuardFileReader.read(Files.newBufferedReader(getClientMappings().toPath()), tree);
                    ProGuardFileReader.read(Files.newBufferedReader(getServerMappings().toPath()), tree);
                    tree.accept(new ProGuardFileWriter(buffer));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public MappingProvider getMappingProvider() {
            return gameData.getMappingProvider();
        }
    }

    @Override
    public String getVersionManifestUrl() {
        return "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    }

    /**
     * @implNote The first version to support official mappings is 1.14.4, but first snapshot to do so was 19w36a
     * @return A list of all versions that have official Mappings(Mojmaps) and can be decompiled
     */
    @Override
    public List<GameVersion> getAllVersions() {
        return GameVersion.from(GameVersion.V1_14_4);
    }
}

package lol.koblizek.amber.platform.format;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.koblizek.amber.platform.GameVersion;
import lol.koblizek.amber.platform.MappingProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
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
        try (var manifestReader = new InputStreamReader(new URI(getVersionManifestUrl()).toURL().openStream())) {
            JsonObject manifest = gson.fromJson(manifestReader, JsonObject.class);
            for (JsonElement versions : manifest.getAsJsonArray("versions")) {
                JsonObject versionObject = versions.getAsJsonObject();
                if (versionObject.get("id").getAsString().equals(version.toString())) {
                    try (var versionReader = new InputStreamReader(new URI(versionObject.get("url").getAsString()).toURL().openStream())) {
                        JsonObject versionData = gson.fromJson(versionReader, JsonObject.class);
                        return new MojangGameDataProvider(versionData, version);
                    }
                }
            }
            return null;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static class MojangGameDataProvider implements GameDataProvider {

        private final JsonObject json;
        private final GameVersion version;

        MojangGameDataProvider(JsonObject json, GameVersion version) {
            this.json = json;
            this.version = version;
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
            return new AssetIndex(json.getAsJsonObject("assetIndex").getAsJsonPrimitive("id").getAsInt(),
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
            return version;
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

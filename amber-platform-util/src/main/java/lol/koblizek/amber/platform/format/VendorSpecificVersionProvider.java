package lol.koblizek.amber.platform.format;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lol.koblizek.amber.platform.GameVersion;
import lol.koblizek.amber.platform.MappingProvider;

import java.util.List;

/**
 * Represents a provider to the game files for a specific provider,
 * such include Fabric, Forge or Official Mojang.
 */
public interface VendorSpecificVersionProvider {
    Gson gson = new Gson();

    MappingProvider getAsMappingProvider();
    String getName();
    GameDataProvider getMinecraftData(GameVersion version);
    String getVersionManifestUrl();

    /**
     * @return A list of all versions that are available for the provider and can be decompiled
     */
    List<GameVersion> getAllVersions();

    interface GameDataProvider {
        String getJsonUrl();

        List<Library> getLibraries();
        String getPathToClientMainClass();
        int getMinimumJavaVersion();
        AssetIndex getAssetIndex();
        String getClientJarUrl();
        String getServerJarUrl();
        String getClientMappingsUrl();
        String getServerMappingsUrl();
        GameVersion getVersion();
    }

    /**
     * Represents a library that is required for the game to run.
     * Such library will be downloaded and added to the classpath of the game.
     *
     * @param name The name of the library, in standard Minecraft manifest, it's represented as "path" field
     * @param artifact The artifact name, in standard Minecraft manifest, it's represented as "name" field
     * @param action The action to be performed with the library, can be null, if the library is not platform specific. This parameter is mostly used at Native libraries
     * @param isNative Specifies if the library is native or not
     */
    record Library(String name, String artifact, LibraryAction action, boolean isNative) {
        public enum LibraryAction {
            ONLY_LINUX,
            ONLY_WINDOWS,
            ONLY_MAC;

            public static LibraryAction getCorresponding(JsonObject libraryObject) {
                if (libraryObject.has("rules")) {
                    for (var rule : libraryObject.getAsJsonArray("rules")) {
                        var ruleObject = rule.getAsJsonObject();
                        var osName = ruleObject.getAsJsonObject("os").get("name").getAsString();
                        return switch (osName) {
                            case "linux" -> ONLY_LINUX;
                            case "windows" -> ONLY_WINDOWS;
                            case "osx" -> ONLY_MAC;
                            default -> {
                                System.err.println("Unknown OS: " + osName + ", please report to developer");
                                yield null;
                            }
                        };
                    }
                }
                return null;
            }
        }
    }

    /**
     * Represents an asset index, which is a json file containing information about assets
     * @param id The id of the asset index
     * @param url The url to the asset index json
     */
    record AssetIndex(int id, String url) {
        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }
}

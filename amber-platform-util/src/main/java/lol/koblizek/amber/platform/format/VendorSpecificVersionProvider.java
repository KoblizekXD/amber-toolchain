package lol.koblizek.amber.platform.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.koblizek.amber.platform.GameVersion;
import lol.koblizek.amber.platform.MappingProvider;
import lol.koblizek.amber.platform.util.GameDataProviderSerializer;
import lol.koblizek.amber.platform.util.Os;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a provider to the game files for a specific provider,
 * such include Fabric, Forge or Official Mojang.
 */
public interface VendorSpecificVersionProvider {
    Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(GameDataProvider.class, new GameDataProviderSerializer()).create();

    MappingProvider getAsMappingProvider();
    String getName();
    GameDataProvider getMinecraftData(GameVersion version);
    String getVersionManifestUrl();

    /**
     * @return A list of all versions that are available for the provider and can be decompiled
     */
    List<GameVersion> getAllVersions();

    interface GameDataProvider {
        Gson localGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().registerTypeAdapter(GameDataProvider.class, new GameDataProviderSerializer()).create();
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
        MappingProvider getMappingProvider();
        List<ArgumentPart> getGameArguments();
        List<ArgumentPart> getJvmArguments();
        <T extends GameDataProvider> MappingProcessor<T> getMappingProcessor(File client, File server);

        default String getAsString() {
            return localGson.toJson(this, GameDataProvider.class);
        }
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

        /**
         * @param os The operating system to check
         * @return True if the library action requires the specific os, false otherwise
         */
        public boolean matchesOs(Os os) {
            if (action == null) return true;
            return switch (action) {
                case ONLY_LINUX -> os == Os.LINUX;
                case ONLY_WINDOWS -> os == Os.WINDOWS;
                case ONLY_MAC -> os == Os.MAC;
            };
        }

        /**
         * Represents an action to be performed with the library, it's
         * mainly used to filter out platform specific libraries or natives
         * @implNote This enum will be later merged into {@link ProvidingAction}
         */
        public enum LibraryAction {
            ONLY_LINUX,
            ONLY_WINDOWS,
            ONLY_MAC;

            /**
             * Parses the library object and returns the corresponding action for the library.
             * Library actions allow to specify platform specific libraries/native sources.
             *
             * @see LibraryAction
             * @param libraryObject The library object to parse
             * @return The corresponding action for the library, or null if no action present
             */
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

    enum ProvidingAction {
        ONLY_LINUX("os.name", "linux"),
        ONLY_WINDOWS("os.name", "windows"),
        ONLY_MAC("os.name", "osx"),
        DEMO_ONLY("features.is_demo_user", "true"),
        HAS_CUSTOM_RESOLUTION("features.has_custom_resolution", "true"),
        IS_X86("os.arch", "x86"),
        IS_WIN10("os.version", "^10\\\\.");

        private final String on;
        private final String expValue;

        ProvidingAction(String on, String expValue) {
            this.on = on;
            this.expValue = expValue;
        }

        public String getExpValue() {
            return expValue;
        }

        public String getOn() {
            return on;
        }

        public static ProvidingAction parseObject(JsonObject object) {
            for (ProvidingAction value : values()) {
                if (inner(object, value.on) != null && inner(object, value.on).getAsString().matches(value.expValue)) {
                    return value;
                }
            }
            return null;
        }
    }

    record ArgumentPart(ProvidingAction action, String... value) {
        public boolean isReplaceable() {
            return value[0].matches("\\$\\{.*}");
        }

        public static ArgumentPart create(JsonElement e) {
            if (e.isJsonPrimitive()) {
                return new ArgumentPart(null, e.getAsString());
            } else {
                JsonObject obj = e.getAsJsonObject();
                String[] values;
                // Here we parse the value/s part of the argument
                if (obj.get("value").isJsonPrimitive())
                    values = new String[] {obj.get("value").getAsString()};
                else {
                    List<String> tmp = new ArrayList<>();
                    obj.get("value").getAsJsonArray().forEach(t -> tmp.add(t.getAsString()));
                    values = tmp.toArray(String[]::new);
                }
                // Here is where we parse the ruleset part of the argument
                ProvidingAction action = null;
                if (obj.has("rules")) {
                    action = ProvidingAction.parseObject(obj.getAsJsonArray("rules").get(0)
                            .getAsJsonObject());
                }
                return new ArgumentPart(
                        action,
                        values
                );
            }
        }
    }

    /**
     * Represents an asset index, which is a json file containing information about assets
     * @param id The id of the asset index
     * @param url The url to the asset index json
     */
    record AssetIndex(String id, String url) {
        @Override
        public String toString() {
            return id;
        }
    }

    /**
     * Parses the inner json element of a json object
     * @param e The json element to work with
     * @param path The path to the inner element
     * @return The inner element, or null if not found
     */
    static JsonElement inner(JsonElement e, String path) {
        String[] parts = path.split("\\.");
        try {
            for (String part : parts) {
                e = e.getAsJsonObject().get(part);
            }
        } catch (Exception ex) {
            return null;
        }
        return e;
    }
}

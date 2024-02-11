package lol.koblizek.amber.platform;

import java.util.List;

/**
 * Represents a Minecraft version as enum
 */
public enum GameVersion {
    V1_10("1.10"),
    V1_11("1.11"),
    V1_12("1.12"),
    V1_12_2("1.12.2"),
    V1_13("1.13"),
    V1_13_2("1.13.2"),
    V1_14("1.14"),
    V1_14_4("1.14.4"),
    V1_15("1.15"),
    V1_15_2("1.15.2"),
    V1_16("1.16"),
    V1_16_1("1.16.1"),
    V1_16_2("1.16.2"),
    V1_16_3("1.16.3"),
    V1_16_4("1.16.4"),
    V1_16_5("1.16.5"),
    V1_17("1.17"),
    V1_17_1("1.17.1"),
    V1_17_2("1.17.2"),
    V1_18("1.18"),
    V1_18_1("1.18.1"),
    V1_18_2("1.18.2"),
    V1_19("1.19"),
    V1_19_1("1.19.1"),
    V1_19_2("1.19.2"),
    V1_19_3("1.19.3"),
    V1_19_4("1.19.4"),
    V1_20("1.20"),
    V1_20_1("1.20.1"),
    V1_20_2("1.20.2"),
    V1_20_3("1.20.3"),
    V1_8("1.8"),
    V1_8_8("1.8.8"),
    V1_8_9("1.8.9"),
    V1_9("1.9"),
    V1_9_4("1.9.4");
    private final String version;

    GameVersion(String version) {

        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }

    /**
     * Get the version from the name
     * @param name The name of the version
     * @return Parsed version or null if not found
     */
    public static GameVersion getProviding(String name) {
        for (GameVersion version : values()) {
            if (version.toString().equals(name)) {
                return version;
            }
        }
        return null;
    }

    /**
     * Get a list of versions that are in the range
     * @param from The starting version(inclusive)
     * @param to The ending version(inclusive)
     * @return A list of versions that are in the range
     */
    public static List<GameVersion> ranging(GameVersion from, GameVersion to) {
        return List.of(values()).subList(from.ordinal(), to.ordinal() + 1);
    }

    /**
     * Get a list of versions that are from the starting version to the latest
     * @param from Starting version(inclusive)
     * @return A list of versions that are from the starting version to the latest
     */
    public static List<GameVersion> from(GameVersion from) {
        return List.of(values()).subList(from.ordinal(), values().length);
    }
}

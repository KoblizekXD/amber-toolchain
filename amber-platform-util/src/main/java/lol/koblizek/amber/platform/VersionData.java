package lol.koblizek.amber.platform;

public record VersionData(GameVersion version, MappingProvider mappings) {
    public VersionData {
        if (mappings == null)
            throw new IllegalArgumentException("No such mapping provider found");
        else if (version == null)
            throw new IllegalArgumentException("No such version found, make sure you're using latest version");
    }
}

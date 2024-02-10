package lol.koblizek.amber.platform;

public enum MappingProvider {
    OFFICIAL("official"),
    MOJANG("mojang"),
    YARN("yarn"),
    MCP("mcp");

    private final String name;

    MappingProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MappingProvider get(String name) {
        for (MappingProvider provider : values()) {
            if (provider.getName().equals(name)) {
                return provider;
            }
        }
        return null;
    }
}

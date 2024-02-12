package lol.koblizek.amber.platform;

import lol.koblizek.amber.platform.format.OfficialVendorProvider;
import lol.koblizek.amber.platform.format.VendorSpecificVersionProvider;

public enum MappingProvider {
    OFFICIAL("official", new OfficialVendorProvider()),
    MOJANG("mojang"),
    YARN("yarn"),
    MCP("mcp");

    private final String name;
    private final VendorSpecificVersionProvider versionProvider;

    MappingProvider(String name, VendorSpecificVersionProvider versionProvider) {
        this.name = name;
        this.versionProvider = versionProvider;
    }

    MappingProvider(String name) {
        this.name = name;
        this.versionProvider = null;
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

    public VendorSpecificVersionProvider getVersionProvider() {
        if (versionProvider == null) {
            throw new UnsupportedOperationException("This provider does not yet have a version provider");
        }
        return versionProvider;
    }
}

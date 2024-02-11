package lol.koblizek.amber.platform.test;

import lol.koblizek.amber.platform.GameVersion;
import lol.koblizek.amber.platform.format.OfficialVendorProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfficialVendorProviderTest {

    private static OfficialVendorProvider provider;

    @BeforeAll
    static void createProvider() {
        provider = new OfficialVendorProvider();
    }

    @Test
    void testReturningGameData() {
        assertNotNull(provider.getMinecraftData(GameVersion.V1_17_1));
        assertNull(provider.getMinecraftData(GameVersion.V1_12));
        var version = provider.getMinecraftData(GameVersion.V1_19_4);
        assertEquals(17, version.getMinimumJavaVersion());
        assertEquals("https://piston-meta.mojang.com/v1/packages/811aef00237a8c91f7d3cfea79ee0414f57ae1b3/1.19.4.json", version.getJsonUrl());
        assertEquals(88, version.getLibraries().size());
        assertEquals(3, version.getAssetIndex().id());
    }
}

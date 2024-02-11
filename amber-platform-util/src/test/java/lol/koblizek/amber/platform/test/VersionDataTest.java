package lol.koblizek.amber.platform.test;


import lol.koblizek.amber.platform.VersionData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionDataTest {
    @Test
    void testVersionDataNullError() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VersionData(null, null);
        });
    }
}

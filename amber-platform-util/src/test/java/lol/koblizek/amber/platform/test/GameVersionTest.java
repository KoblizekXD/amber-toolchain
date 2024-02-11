package lol.koblizek.amber.platform.test;

import lol.koblizek.amber.platform.GameVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameVersionTest {

    @Test
    void testRanging() {
        Assertions.assertEquals(GameVersion.ranging(GameVersion.V1_10, GameVersion.V1_12_2).size(), 4);
    }

    @Test
    void testFrom() {
        Assertions.assertTrue(GameVersion.from(GameVersion.V1_10).contains(GameVersion.V1_11));
        Assertions.assertFalse(GameVersion.from(GameVersion.V1_10).contains(GameVersion.V1_8_9));
    }
}

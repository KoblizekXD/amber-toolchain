package lol.koblizek.amber.platform.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lol.koblizek.amber.platform.format.VendorSpecificVersionProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VendorSpecificVersionProviderTest {
    @Test
    void testActionProvider() {
        JsonObject object = new Gson().fromJson("""
                {
                            "action": "allow",
                            "os": {
                              "name": "windows"
                            }
                          }""", JsonObject.class);
        Assertions.assertNotNull(VendorSpecificVersionProvider.ProvidingAction.parseObject(object));
        Assertions.assertEquals(VendorSpecificVersionProvider.ProvidingAction.ONLY_WINDOWS, VendorSpecificVersionProvider.ProvidingAction.parseObject(object));
    }
}

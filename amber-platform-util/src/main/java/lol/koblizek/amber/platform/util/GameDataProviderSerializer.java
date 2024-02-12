package lol.koblizek.amber.platform.util;

import com.google.gson.*;
import lol.koblizek.amber.platform.format.VendorSpecificVersionProvider;

import java.lang.reflect.Type;

public class GameDataProviderSerializer implements JsonSerializer<VendorSpecificVersionProvider.GameDataProvider> {
    @Override
    public JsonElement serialize(VendorSpecificVersionProvider.GameDataProvider src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("version", src.getVersion().toString());
        object.addProperty("json", src.getJsonUrl());
        object.addProperty("clientMain", src.getPathToClientMainClass());
        object.addProperty("minJava", src.getMinimumJavaVersion());
        object.add("assetIndex", gson.toJsonTree(src.getAssetIndex()));
        object.add("libraries", gson.toJsonTree(src.getLibraries()));
        object.addProperty("clientJar", src.getClientJarUrl());
        object.addProperty("serverJar", src.getServerJarUrl());
        object.addProperty("clientMappings", src.getClientMappingsUrl());
        object.addProperty("serverMappings", src.getServerMappingsUrl());
        JsonObject arguments = new JsonObject();
        arguments.add("game", gson.toJsonTree(src.getGameArguments()));
        arguments.add("jvm", gson.toJsonTree(src.getJvmArguments()));
        object.add("args", arguments);
        return object;
    }
}

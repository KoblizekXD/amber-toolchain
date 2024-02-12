package lol.koblizek.amber.platform;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Was only used to test the enum generation
 */
@Deprecated
public class Main {
    public static void main(String[] args) throws Exception {
        // Like what even is this? I don't even know what this is supposed to do
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(new InputStreamReader(new URI("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").toURL().openStream()), JsonObject.class);
        List<JsonObject> objects = new ArrayList<>();
        obj.getAsJsonArray("versions").forEach(e -> objects.add(e.getAsJsonObject()));
        objects.stream().map(e -> e.get("id").getAsString()).map(e -> "V" + e.replace("-", "_").replace(".", "_").replace(" ", "_").toUpperCase()
                + "(\"" + e + "\"),").forEach(System.out::println);
    }
}

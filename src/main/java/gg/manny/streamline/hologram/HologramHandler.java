package gg.manny.streamline.hologram;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.streamline.Streamline;
import lombok.Cleanup;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HologramHandler {

    public void save() {
        File file = getFile();
        try {
            @Cleanup FileWriter writer = new FileWriter(file);
            JsonArray jsonArray = new JsonArray();
            HologramRegistry.getHolograms().values().forEach(holo -> jsonArray.add(holo.serialize()));

            Streamline.GSON.toJson(jsonArray, writer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void load() throws Exception {
        HologramRegistry.getHolograms().clear();
        Bukkit.getServer().getLogger().info("[Hologram Handler] Loading holograms...");
        File file = getFile();
        @Cleanup FileReader reader = new FileReader(file);
        JsonElement element = new JsonParser().parse(reader);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            array.forEach(obj -> {
                JsonObject data = obj.getAsJsonObject();
                HologramRegistry.getHolograms().put(data.get("id").getAsString(), new CraftHologram(data));
            });
        }

        Bukkit.getServer().getLogger().info("[Hologram Handler] Loaded " + HologramRegistry.getHolograms().size() + " holograms.");
        save();

    }

    private File getFile() {
        File file = new File(Streamline.getInstance().getDataFolder() + File.separator + "holograms.json");
        if (!file.exists()) {
            try {
                Bukkit.getServer().getLogger().info("[Hologram Handler] Creating holograms.json file...");
                file.createNewFile();
            } catch (IOException ignored) { }
        }
        return file;
    }

}

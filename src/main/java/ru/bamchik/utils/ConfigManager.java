package ru.bamchik.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.gui.ClickGUI;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final File CONFIG_FILE = new File("config/bamchik_client.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveConfig() {
        ConfigData data = new ConfigData();
        data.scale = ClickGUI.scale;
        data.binds = new HashMap<>();
        if (BamchikClient.getInstance() != null) {
            for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
                if (m.getKeyBind() != 0) data.binds.put(m.getName(), m.getKeyBind());
            }
        }
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) return;
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = gson.fromJson(reader, ConfigData.class);
            ClickGUI.scale = data.scale;
            if (data.binds != null && BamchikClient.getInstance() != null) {
                for (Map.Entry<String, Integer> e : data.binds.entrySet()) {
                    Module m = BamchikClient.getInstance().getModuleManager().getModuleByName(e.getKey());
                    if (m != null) m.setKeyBind(e.getValue());
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    static class ConfigData {
        float scale = 1.0f;
        Map<String, Integer> binds = new HashMap<>();
    }
}

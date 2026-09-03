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
        data.guiColor = ClickGUI.guiColor;
        data.textColor = ClickGUI.textColor;
        data.bgColor = ClickGUI.bgColor;
        data.scale = ClickGUI.scale;
        data.theme = ClickGUI.theme;
        data.binds = new HashMap<>();
        for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
            if (m.getKeyBind() != 0) data.binds.put(m.getName(), m.getKeyBind());
        }
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) return;
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = gson.fromJson(reader, ConfigData.class);
            ClickGUI.guiColor = data.guiColor;
            ClickGUI.textColor = data.textColor;
            ClickGUI.bgColor = data.bgColor;
            ClickGUI.scale = data.scale;
            ClickGUI.theme = data.theme;
            if (data.binds != null && BamchikClient.getInstance() != null) {
                for (Map.Entry<String, Integer> e : data.binds.entrySet()) {
                    Module m = BamchikClient.getInstance().getModuleManager().getModuleByName(e.getKey());
                    if (m != null) m.setKeyBind(e.getValue());
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    static class ConfigData {
        int guiColor = 0xFF00AAFF;
        int textColor = 0xFFFFFFFF;
        int bgColor = 0xCC000000;
        float scale = 1.0f;
        String theme = "dark";
        Map<String, Integer> binds = new HashMap<>();
    }
}
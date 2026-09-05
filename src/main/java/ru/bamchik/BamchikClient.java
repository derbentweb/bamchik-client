package ru.bamchik;

import net.fabricmc.api.ClientModInitializer;
import ru.bamchik.utils.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BamchikClient implements ClientModInitializer {
    public static final String MOD_NAME = "bamchik client";
    public static final String VERSION = "2.0";
    private static BamchikClient instance;
    private ModuleManager moduleManager;
    private boolean keyValid = false;

    @Override
    public void onInitializeClient() {
        instance = this;

        moduleManager = new ModuleManager();
        moduleManager.initModules();

        if (!checkLicense()) {
            System.err.println("[" + MOD_NAME + "] Ключ не найден или неверен. Клиент запущен без функций.");
            keyValid = false;
            return;
        }
        keyValid = true;

        ConfigManager.loadConfig();

        System.out.println("[" + MOD_NAME + "] v" + VERSION + " успешно загружен!");
    }

    private boolean checkLicense() {
        File keyFile = new File("keys.txt");
        String key = null;

        if (keyFile.exists()) {
            try {
                key = new String(Files.readAllBytes(Paths.get(keyFile.getPath()))).trim();
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Ошибка чтения keys.txt");
            }
        }

        if (key == null || key.isEmpty()) {
            try (FileWriter writer = new FileWriter(keyFile)) {
                writer.write("test-key");
                key = "test-key";
                System.out.println("[" + MOD_NAME + "] Создан keys.txt со стандартным ключом.");
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Не удалось создать keys.txt");
                return false;
            }
        }

        return !key.isEmpty();
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

package ru.bamchik;

import net.fabricmc.api.ModInitializer;
import ru.bamchik.license.LicenseManager;
import ru.bamchik.utils.ConfigManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BamchikClient implements ModInitializer {
    public static final String MOD_NAME = "bamchik client";
    public static final String VERSION = "1.0";
    private static BamchikClient instance;
    private ModuleManager moduleManager;
    private boolean keyValid = false;

    @Override
    public void onInitialize() {
        instance = this;
        ConfigManager.loadConfig();
        if (!checkLicense()) {
            System.err.println("[" + MOD_NAME + "] Ключ не найден или неверен. Клиент запущен без функций.");
            keyValid = false;
            return;
        }
        keyValid = true;
        moduleManager = new ModuleManager();
        moduleManager.initModules();
        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private boolean checkLicense() {
        File keyFile = new File("keys.txt");
        String key = null;

        if (keyFile.exists()) {
            try {
                key = new String(Files.readAllBytes(Paths.get(keyFile.getPath()))).trim();
                System.out.println("[" + MOD_NAME + "] Ключ прочитан: " + key);
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Ошибка чтения keys.txt");
            }
        }

        if (key == null || key.isEmpty()) {
            try {
                FileWriter writer = new FileWriter(keyFile);
                writer.write("test-key");
                writer.close();
                System.out.println("[" + MOD_NAME + "] Создан keys.txt со стандартным ключом. Перезапустите игру.");
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Не удалось создать keys.txt");
            }
            return false;
        }

        // Локальная проверка – любой непустой ключ считается валидным
        return !key.isEmpty();
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

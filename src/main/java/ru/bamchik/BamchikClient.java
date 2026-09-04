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
            System.err.println("[" + MOD_NAME + "] Лицензионная проверка не пройдена. Клиент запущен без функций читера.");
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

        // Пытаемся прочитать ключ из файла
        if (keyFile.exists()) {
            try {
                key = new String(Files.readAllBytes(Paths.get(keyFile.getPath()))).trim();
                System.out.println("[" + MOD_NAME + "] Ключ прочитан из файла: " + key);
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Ошибка чтения keys.txt: " + e.getMessage());
            }
        }

        // Если ключа нет в файле, создаём файл с инструкцией
        if (key == null || key.isEmpty()) {
            System.err.println("[" + MOD_NAME + "] Файл keys.txt не найден или пуст. Создаём файл-заглушку.");
            try {
                FileWriter writer = new FileWriter(keyFile);
                writer.write("Вставьте сюда ваш лицензионный ключ (или любой текст) и перезапустите игру.");
                writer.close();
                System.out.println("[" + MOD_NAME + "] Создан файл keys.txt в папке .minecraft. Добавьте ключ и перезапустите игру.");
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Не удалось создать keys.txt: " + e.getMessage());
            }
            return false;
        }

        // Проверяем ключ через LicenseManager
        boolean valid = LicenseManager.checkLicense(key);
        if (!valid) {
            System.err.println("[" + MOD_NAME + "] Неверный ключ! Доступ запрещён.");
            return false;
        }
        return true;
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

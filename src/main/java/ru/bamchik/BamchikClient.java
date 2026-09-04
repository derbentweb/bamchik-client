package ru.bamchik;

import net.fabricmc.api.ModInitializer;
import ru.bamchik.license.LicenseManager;
import ru.bamchik.utils.ConfigManager;

import javax.swing.JOptionPane;
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
            System.err.println("[" + MOD_NAME + "] Лицензионная проверка не пройдена. Завершение работы.");
            System.exit(0);
        }
        moduleManager = new ModuleManager();
        moduleManager.initModules();
        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private boolean checkLicense() {
        String key = null;
        // 1. Пытаемся прочитать ключ из файла keys.txt в папке игры
        File keyFile = new File("keys.txt");
        if (keyFile.exists()) {
            try {
                key = new String(Files.readAllBytes(Paths.get(keyFile.getPath()))).trim();
                System.out.println("[" + MOD_NAME + "] Ключ прочитан из файла: " + key);
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Ошибка чтения keys.txt: " + e.getMessage());
            }
        }
        // 2. Если ключ не получен из файла, показываем диалог
        if (key == null || key.isEmpty()) {
            try {
                key = JOptionPane.showInputDialog(null,
                        "Введите лицензионный ключ для " + MOD_NAME + ":",
                        "Активация", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception e) {
                System.err.println("[" + MOD_NAME + "] Не удалось показать диалог ввода ключа: " + e.getMessage());
                return false;
            }
        }
        if (key == null || key.isEmpty()) {
            System.err.println("[" + MOD_NAME + "] Ключ не введён.");
            return false;
        }
        boolean valid = LicenseManager.checkLicense(key);
        if (!valid) {
            System.err.println("[" + MOD_NAME + "] Неверный ключ! Доступ запрещён.");
            return false;
        }
        keyValid = true;
        return true;
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

package ru.bamchik;

import net.fabricmc.api.ModInitializer;
import ru.bamchik.license.LicenseManager;
import ru.bamchik.utils.ConfigManager;

import javax.swing.JOptionPane;

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
            System.exit(0);
        }
        moduleManager = new ModuleManager();
        moduleManager.initModules();
        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private boolean checkLicense() {
        String key = JOptionPane.showInputDialog(null,
                "Введите лицензионный ключ для " + MOD_NAME + ":",
                "Активация", JOptionPane.PLAIN_MESSAGE);
        if (key == null) {
            JOptionPane.showMessageDialog(null, "Ключ не введён. Клиент закрывается.");
            return false;
        }
        boolean valid = LicenseManager.checkLicense(key);
        if (!valid) {
            JOptionPane.showMessageDialog(null, "Неверный ключ! Доступ запрещён.");
            return false;
        }
        keyValid = true;
        return true;
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}
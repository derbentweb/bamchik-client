package ru.bamchik;

import net.fabricmc.api.ModInitializer;
import ru.bamchik.license.LicenseManager;
import ru.bamchik.utils.ConfigManager;

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
            System.out.println("[" + MOD_NAME + "] Критическая ошибка: Неверный ключ лицензии!");
            return; 
        }
        
        moduleManager = new ModuleManager();
        moduleManager.initModules();
        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private boolean checkLicense() {
        // Временно прописали статичный ключ, так как в ConfigManager нет нужного метода
        String key = "BAMCHIK-FREE-KEY"; 
        
        if (key == null || key.isEmpty()) {
            System.out.println("[" + MOD_NAME + "] Лицензионный ключ не обнаружен!");
            return false;
        }
        
        boolean valid = LicenseManager.checkLicense(key);
        if (!valid) {
            System.out.println("[" + MOD_NAME + "] Доступ запрещен: Ключ " + key + " невалиден.");
            return false;
        }
        
        keyValid = true;
        return true;
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

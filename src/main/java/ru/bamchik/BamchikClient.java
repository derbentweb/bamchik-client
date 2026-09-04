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
        
        // Получаем ключ из конфигурации или аргументов запуска
        if (!checkLicense()) {
            System.out.println("[" + MOD_NAME + "] Критическая ошибка: Неверный ключ лицензии!");
            // Вместо жесткого System.exit(0), который может уронить лаунчер при загрузке,
            // мы просто отменяем инициализацию чит-функций.
            return; 
        }
        
        moduleManager = new ModuleManager();
        moduleManager.initModules();
        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private boolean checkLicense() {
        // Читаем ключ, который сохранен в вашем ConfigManager
        String key = ConfigManager.getLicenseKey(); 
        
        if (key == null || key.isEmpty()) {
            System.out.println("[" + MOD_NAME + "] Лицензионный ключ не обнаружен в config.txt!");
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

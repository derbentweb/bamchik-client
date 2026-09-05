package ru.bamchik;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.bamchik.gui.ClickGUI;
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

    private static KeyBinding guiKeyBinding;

    @Override
    public void onInitializeClient() {
        instance = this;

        // 1. Инициализация менеджера модулей
        moduleManager = new ModuleManager();
        moduleManager.initModules();

        // 2. Проверка лицензионного ключа
        if (!checkLicense()) {
            System.err.println("[" + MOD_NAME + "] Ключ не найден или неверен. Клиент запущен без функций.");
            keyValid = false;
            return;
        }
        keyValid = true;

        // 3. Загрузка конфигураций (после инициализации модулей)
        ConfigManager.loadConfig();

        // 4. Регистрация клавиши открытия ClickGUI (по умолчанию Right Shift)
        guiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bamchik.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.bamchik.title"
        ));

        // 5. Главный игровой цикл обработчика тиков
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (guiKeyBinding.wasPressed()) {
                client.setScreen(new ClickGUI());
            }

            if (moduleManager != null) {
                moduleManager.onTick();
            }
        });

        // 6. Отрисовка интерфейса и визуалов (HUD)
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            if (moduleManager != null) {
                moduleManager.onHudRender(drawContext, renderTickCounter.getTickDelta(true));
            }
        });

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

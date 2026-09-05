package ru.bamchik;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.bamchik.gui.ClickGUI;
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
    private boolean isKeyPressed = false;

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

        // Регистрация слушателя событий тика для отслеживания нажатия Right Shift
        registerKeyBindings();

        System.out.println("[" + MOD_NAME + "] Загружен успешно!");
    }

    private void registerKeyBindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!keyValid || client.player == null) return;

            long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
            boolean rightShiftPressed = InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT);

            if (rightShiftPressed && !isKeyPressed) {
                if (!(client.currentScreen instanceof ClickGUI)) {
                    client.setScreen(new ClickGUI());
                }
                isKeyPressed = true;
            } else if (!rightShiftPressed) {
                isKeyPressed = false;
            }
        });
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
            try (FileWriter writer = new FileWriter(keyFile)) {
                writer.write("test-key");
                System.out.println("[" + MOD_NAME + "] Создан keys.txt со стандартным ключом. Перезапустите игру.");
            } catch (IOException e) {
                System.err.println("[" + MOD_NAME + "] Не удалось создать keys.txt");
            }
            return false;
        }

        return !key.isEmpty();
    }

    public static BamchikClient getInstance() { return instance; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isKeyValid() { return keyValid; }
}

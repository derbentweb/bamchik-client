package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.gui.ClickGUI;

import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    private boolean wasRightShiftPressed = false;
    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;

        if (BamchikClient.getInstance() == null || !BamchikClient.getInstance().isKeyValid()) {
            return;
        }

        // Обновление состояния всех модулей
        if (BamchikClient.getInstance().getModuleManager() != null) {
            BamchikClient.getInstance().getModuleManager().onTick();

            // Переключение модулей по биндам без вызова Thread.sleep
            long window = client.getWindow().getHandle();
            if (client.currentScreen == null) {
                for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
                    int key = m.getKeyBind();
                    if (key > 0) {
                        boolean isPressed = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
                        boolean wasPressed = keyStates.getOrDefault(key, false);

                        if (isPressed && !wasPressed) {
                            m.setEnabled(!m.isEnabled());
                        }
                        keyStates.put(key, isPressed);
                    }
                }
            }
        }

        // Открытие и закрытие ClickGUI по нажатию Right Shift
        long window = client.getWindow().getHandle();
        boolean isRightShiftPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        if (isRightShiftPressed && !wasRightShiftPressed) {
            if (client.currentScreen == null) {
                client.setScreen(new ClickGUI());
            } else if (client.currentScreen instanceof ClickGUI) {
                client.setScreen(null);
            }
        }
        wasRightShiftPressed = isRightShiftPressed;
    }
}

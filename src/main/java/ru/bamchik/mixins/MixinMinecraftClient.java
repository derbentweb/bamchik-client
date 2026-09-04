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

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    private boolean wasRightShiftPressed = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (BamchikClient.getInstance() != null && BamchikClient.getInstance().isKeyValid()) {
            // Вызов тиков модулей
            BamchikClient.getInstance().getModuleManager().onTick();

            // Обработка биндов (клавиши для модулей)
            for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
                int key = m.getKeyBind();
                if (key != 0 && MinecraftClient.getInstance().currentScreen == null) {
                    long window = MinecraftClient.getInstance().getWindow().getHandle();
                    if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
                        // Простейший debounce – можно добавить задержку, но для простоты оставим
                        m.setEnabled(!m.isEnabled());
                        // Небольшая пауза, чтобы не переключалось несколько раз за одно нажатие
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    }
                }
            }

            // Открытие ClickGUI по правому Shift (однократное нажатие)
            long window = MinecraftClient.getInstance().getWindow().getHandle();
            boolean isRightShiftPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (isRightShiftPressed && !wasRightShiftPressed && MinecraftClient.getInstance().currentScreen == null) {
                MinecraftClient.getInstance().setScreen(new ClickGUI());
            }
            wasRightShiftPressed = isRightShiftPressed;
        }
    }
}

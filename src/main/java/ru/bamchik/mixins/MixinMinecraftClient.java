package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.gui.ClickGUI;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    
    // Переменная, чтобы кнопка не срабатывала миллион раз за одно зажатие
    private static boolean isKeyPressed = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.window == null) return;

        // Самый безопасный и чистый вызов через чистое окно GLFW без ломающихся методов Майнкрафта
        long windowHandle = mc.getWindow().getHandle();
        boolean isDown = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        if (isDown) {
            if (!isKeyPressed && mc.currentScreen == null) {
                isKeyPressed = true;
                // Безопасно открываем меню ClickGUI в потоке игры
                mc.execute(() -> mc.setScreen(new ClickGUI()));
            }
        } else {
            isKeyPressed = false;
        }
    }
}

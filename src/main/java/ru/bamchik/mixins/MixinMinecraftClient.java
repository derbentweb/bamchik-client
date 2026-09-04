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

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void onHandleInputEvents(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.window == null) return;

        // Считываем нажатие RShift в официальном потоке обработки ввода игры (handleInputEvents)
        long windowHandle = mc.getWindow().getHandle();
        boolean isDown = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        // Открываем экран только если сейчас не открыто другое меню (чат или инвентарь)
        if (isDown && mc.currentScreen == null) {
            // Перенаправляем открытие в безопасный отложенный поток рендеринга, чтобы избежать конфликта тиков сервера
            mc.execute(() -> {
                if (mc.currentScreen == null) {
                    mc.setScreen(new ClickGUI());
                }
            });
        }
    }
}

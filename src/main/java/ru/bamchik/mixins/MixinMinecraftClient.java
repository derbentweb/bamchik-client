package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.gui.ClickGUI;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        // Проверяем нажатие Правого Шифта (RShift) через современный движок GLFW в 1.21.1
        long windowHandle = mc.getWindow().getHandle();
        if (InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            // Открываем ваше меню читов ClickGUI
            mc.execute(() -> mc.setScreen(new ClickGUI()));
        }
    }
}

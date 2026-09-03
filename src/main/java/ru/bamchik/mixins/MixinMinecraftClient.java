package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.gui.ClickGUI;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    private long lastPress = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (BamchikClient.getInstance() != null && BamchikClient.getInstance().isKeyValid()) {
            BamchikClient.getInstance().getModuleManager().onTick();

            // Бинды
            for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
                int key = m.getKeyBind();
                if (key != 0 && MinecraftClient.getInstance().currentScreen == null) {
                    long window = MinecraftClient.getInstance().getWindow().getHandle();
                    if (org.lwjgl.glfw.GLFW.glfwGetKey(window, key) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                        if (System.currentTimeMillis() - lastPress > 300) {
                            m.setEnabled(!m.isEnabled());
                            lastPress = System.currentTimeMillis();
                        }
                    }
                }
            }

            // Открытие GUI по RShift (код GLFW_KEY_RIGHT_SHIFT = 344)
            if (MinecraftClient.getInstance().currentScreen == null &&
                org.lwjgl.glfw.GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), 344) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                MinecraftClient.getInstance().setScreen(new ClickGUI());
            }
        }
    }
}
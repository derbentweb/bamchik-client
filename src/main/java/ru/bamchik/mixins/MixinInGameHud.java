package ru.bamchik.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter; // Добавлен правильный класс для 1.21.1
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.BamchikClient;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (BamchikClient.getInstance() != null && BamchikClient.getInstance().isKeyValid()) {
            // Получаем чистый float tickDelta из современного объекта тиков игры
            float tickDelta = tickCounter.getTickDelta(true);
            BamchikClient.getInstance().getModuleManager().onHudRender(context, tickDelta);
        }
    }
}

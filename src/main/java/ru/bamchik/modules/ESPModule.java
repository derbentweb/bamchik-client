package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.client.util.math.MatrixStack;

public class ESPModule extends Module {
    public ESPModule() { super("ESP"); }
    @Override public void onTick() {}
    @Override public void onRender(MatrixStack matrices, float tickDelta) {
        // Рисование рамок через миксин или здесь. Оставим заглушку для простоты.
    }
}
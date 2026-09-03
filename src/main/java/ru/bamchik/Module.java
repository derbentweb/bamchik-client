package ru.bamchik;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module {
    protected MinecraftClient mc = MinecraftClient.getInstance();
    private String name;
    private boolean enabled;
    private int keyBind = 0; // 0 = нет бинда

    public Module(String name) { this.name = name; }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean e) { this.enabled = e; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int key) { this.keyBind = key; }

    public abstract void onTick();
    public void onRender(MatrixStack matrices, float tickDelta) {}
    public void onHudRender(DrawContext context, float tickDelta) {} // для HUD-модулей
}

// Интерфейс для HUD-модулей (чтобы не добавлять метод в Module, если не нужно)
interface HudModule {
    void onHudRender(DrawContext context, float tickDelta);
}
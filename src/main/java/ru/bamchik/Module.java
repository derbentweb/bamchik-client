package ru.bamchik;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String category;
    private int keyBind;
    private boolean enabled;

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
        this.keyBind = 0;
        this.enabled = false;
    }

    public Module(String name, String category, int keyBind) {
        this.name = name;
        this.category = category;
        this.keyBind = keyBind;
        this.enabled = false;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender(MatrixStack matrices, float tickDelta) {}
    public void onHudRender(DrawContext context, float tickDelta) {}

    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getKey() { return keyBind; }
    public int getKeyBind() { return keyBind; }
    public void setKey(int key) { this.keyBind = key; }
    public void setKeyBind(int key) { this.keyBind = key; }
    public boolean isEnabled() { return enabled; }
}

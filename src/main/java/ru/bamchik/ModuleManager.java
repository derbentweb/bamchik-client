package ru.bamchik;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String category;
    private int key;
    private boolean enabled;

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
        this.key = -1;
        this.enabled = false;
    }

    public Module(String name, String category, int key) {
        this.name = name;
        this.category = category;
        this.key = key;
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
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public boolean isEnabled() { return enabled; }
}

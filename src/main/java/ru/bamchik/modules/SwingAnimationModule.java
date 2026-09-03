package ru.bamchik.modules;

import ru.bamchik.Module;

public class SwingAnimationModule extends Module {
    private boolean cancelSwing = false;
    private float swingSpeed = 1.0f;

    public SwingAnimationModule() { super("SwingAnimation"); }

    @Override public void onTick() {}

    public boolean isCancelSwing() { return cancelSwing; }
    public float getSwingSpeed() { return swingSpeed; }
    public void setCancelSwing(boolean cancel) { this.cancelSwing = cancel; }
    public void setSwingSpeed(float speed) { this.swingSpeed = Math.max(0.1f, Math.min(3.0f, speed)); }
}
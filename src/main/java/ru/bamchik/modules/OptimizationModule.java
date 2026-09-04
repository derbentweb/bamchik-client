package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.GraphicsMode;

public class OptimizationModule extends Module {
    private boolean isOptimized = false;
    private int maxFps = 120;
    private int renderDistance = 8;
    private boolean disableClouds = true;
    private boolean disableParticles = true;
    private boolean disableSmoothLighting = true;
    private boolean disableShadows = true;
    private boolean disableEntityShadows = true;
    private boolean useFastRender = true;

    public OptimizationModule() { super("Optimization"); }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        applyOptimization();
    }

    private void applyOptimization() {
        GameOptions options = mc.options;
        if (!isOptimized) isOptimized = true;
        
        options.getMaxFps().setValue(maxFps);
        options.getViewDistance().setValue(renderDistance);
        
        // Исправлено под современные настройки Minecraft 1.21.1
        if (disableClouds) options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        if (disableParticles) options.getParticlesMode().setValue(ParticlesMode.MINIMAL);
        if (disableSmoothLighting) options.getAo().setValue(net.minecraft.client.render.AoMode.OFF);
        if (disableShadows) options.getSimulationDistance().setValue(4); // В качестве оптимизации теней/симуляции
        if (disableEntityShadows) options.getEntityShadows().setValue(false);
        if (useFastRender) options.getGraphicsMode().setValue(GraphicsMode.FAST);
    }

    public void setMaxFps(int fps) { this.maxFps = Math.max(20, fps); }
    public void setRenderDistance(int dist) { this.renderDistance = Math.max(2, Math.min(32, dist)); }
}

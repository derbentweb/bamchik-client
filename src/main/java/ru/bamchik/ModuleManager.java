package ru.bamchik;

import ru.bamchik.modules.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private List<Module> modules = new ArrayList<>();

    public void initModules() {
        add(new FlightModule());
        add(new SpeedModule());
        add(new KillAuraModule());
        add(new XRayModule());
        add(new NukerModule());
        add(new ESPModule());
        add(new AutoClickerModule());
        add(new NoFallModule());
        add(new RandomNickModule());
        add(new OptimizationModule());
        add(new BaseFinderModule());
        add(new TriggerBotModule());
        add(new SwingAnimationModule());
        for (Module m : modules) m.setEnabled(true);
    }

    private void add(Module m) { modules.add(m); }

    public List<Module> getModules() { return modules; }

    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }

    public void onRender(MatrixStack matrices, float tickDelta) {
        for (Module m : modules) {
            if (m.isEnabled()) m.onRender(matrices, tickDelta);
        }
    }

    public void onHudRender(DrawContext context, float tickDelta) {
        for (Module m : modules) {
            if (m.isEnabled() && m instanceof HudModule) {
                ((HudModule) m).onHudRender(context, tickDelta);
            }
        }
    }
}
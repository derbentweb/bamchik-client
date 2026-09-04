package ru.bamchik;

import ru.bamchik.modules.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private List<Module> modules = new ArrayList<>();

    public void initModules() {
        // Combat
        add(new KillAuraModule());
        add(new TriggerBotModule());
        add(new AutoClickerModule());
        // Movement
        add(new FlightModule());
        add(new SpeedModule());
        add(new NoFallModule());
        // Visuals
        add(new XRayModule());
        add(new ESPModule());
        // Misc
        add(new NukerModule());
        add(new RandomNickModule());
        add(new OptimizationModule());
        add(new BaseFinderModule());
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

    // Новые методы для категорий
    public List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        for (Module m : modules) {
            String cat = m.getCategory();
            if (!cats.contains(cat)) cats.add(cat);
        }
        return cats;
    }

    public List<Module> getModulesByCategory(String category) {
        List<Module> list = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory().equalsIgnoreCase(category)) list.add(m);
        }
        return list;
    }

    public void onTick() {
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public void onRender(MatrixStack matrices, float tickDelta) {
        for (Module m : modules) if (m.isEnabled()) m.onRender(matrices, tickDelta);
    }

    public void onHudRender(DrawContext context, float tickDelta) {
        for (Module m : modules) if (m.isEnabled()) m.onHudRender(context, tickDelta);
    }
}

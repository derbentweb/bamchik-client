package ru.bamchik;

import ru.bamchik.modules.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

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
    }

    private void add(Module m) {
        modules.add(m);
    }

    public List<Module> getModules() {
        return modules;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) {
                return (T) m;
            }
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

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

    public void onKeyPressed(int key) {
        if (key <= 0) return;
        for (Module m : modules) {
            if (m.getKeyBind() == key) {
                m.toggle();
            }
        }
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onTick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRender(MatrixStack matrices, float tickDelta) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onRender(matrices, tickDelta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onHudRender(DrawContext context, float tickDelta) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onHudRender(context, tickDelta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

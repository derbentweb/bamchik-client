package ru.bamchik.modules;

import ru.bamchik.Module;

public class NoFallModule extends Module {
    public NoFallModule() { super("NoFall", "Movement"); }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.fallDistance > 3.0f) {
            mc.player.fallDistance = 0;
        }
    }
}

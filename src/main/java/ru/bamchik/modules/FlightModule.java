package ru.bamchik.modules;

import ru.bamchik.Module;

public class FlightModule extends Module {
    public FlightModule() { super("Flight"); }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(0.1f);
        if (mc.options.jumpKey.isPressed()) {
            mc.player.setVelocity(mc.player.getVelocity().add(0, 0.2, 0));
        }
        if (mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(mc.player.getVelocity().add(0, -0.2, 0));
        }
    }
}
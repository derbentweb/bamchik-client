package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.util.math.Vec3d;

public class SpeedModule extends Module {
    public SpeedModule() { super("Speed"); }

    @Override
    public void onTick() {
        if (mc.player == null || !mc.player.isOnGround()) return;
        if (mc.options.forwardKey.isPressed()) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x * 1.8, vel.y, vel.z * 1.8);
        }
    }
}
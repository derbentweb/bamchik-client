package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TriggerBotModule extends Module {
    private double range = 4.5;
    private float fov = 45f;
    private boolean targetPlayers = true;
    private boolean targetMobs = false;
    private boolean swingArm = true;
    private long lastAttack = 0;
    private long delay = 150;

    public TriggerBotModule() { super("TriggerBot", "Combat"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        if (!mc.options.attackKey.isPressed()) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < delay) return;

        Entity target = getTarget();
        if (target == null) return;

        mc.interactionManager.attackEntity(mc.player, target);
        if (swingArm) mc.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }

    private Entity getTarget() {
        HitResult hit = mc.player.raycast(range, 1.0f, false);
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return null;
        Entity entity = ((net.minecraft.util.hit.EntityHitResult) hit).getEntity();
        if (entity == mc.player) return null;
        if (!(entity instanceof LivingEntity)) return null;
        if (entity instanceof PlayerEntity && !targetPlayers) return null;
        if (!(entity instanceof PlayerEntity) && !targetMobs) return null;
        if (!isInFov(entity)) return null;
        return entity;
    }

    private boolean isInFov(Entity target) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d toTarget = target.getBoundingBox().getCenter().subtract(eye).normalize();
        Vec3d lookVec = mc.player.getRotationVector();
        double angle = Math.acos(lookVec.dotProduct(toTarget));
        return Math.toDegrees(angle) <= fov;
    }

    public void setRange(double r) { this.range = Math.max(1, Math.min(6, r)); }
    public void setFov(float f) { this.fov = Math.max(10, Math.min(180, f)); }
    public void setDelay(long d) { this.delay = Math.max(50, d); }
}

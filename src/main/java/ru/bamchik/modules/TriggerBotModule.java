package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TriggerBotModule extends Module {
    private double range = 3.8;
    private boolean targetPlayers = true;
    private boolean targetMobs = false;
    private boolean swingArm = true;
    private boolean checkCooldown = true;

    public TriggerBotModule() {
        super("TriggerBot", "Combat");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;

        // Автоматическая проверка задержки оружия для 100% урона в 1.21.11
        if (checkCooldown && mc.player.getAttackCooldownProgress(0.5f) < 0.92f) {
            return;
        }

        Entity target = getTarget();
        if (target == null) return;

        mc.interactionManager.attackEntity(mc.player, target);
        if (swingArm) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private Entity getTarget() {
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d rotationVec = mc.player.getRotationVector();
        Vec3d reachVec = eyePos.add(rotationVec.multiply(range));
        Box box = mc.player.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0);

        // Точный Raycast сущностей через встроенные утилиты Fabric
        EntityHitResult entityHitResult = ProjectileUtil.raycast(
            mc.player,
            eyePos,
            reachVec,
            box,
            e -> e instanceof LivingEntity living 
                && living.isAlive() 
                && living.getHealth() > 0 
                && !e.isSpectator(),
            range * range
        );

        if (entityHitResult == null) return null;

        Entity entity = entityHitResult.getEntity();
        if (entity == mc.player) return null;

        if (entity instanceof PlayerEntity && !targetPlayers) return null;
        if (!(entity instanceof PlayerEntity) && !targetMobs) return null;

        return entity;
    }

    public void setRange(double r) { this.range = Math.max(1, Math.min(6, r)); }
    public void setCheckCooldown(boolean checkCooldown) { this.checkCooldown = checkCooldown; }
}

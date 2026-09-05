package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class KillAuraModule extends Module {
    private final Random random = new Random();

    public enum RotationMode { NONE, NORMAL, SILENT, SWITCH, RANDOM, HELIXWAVE }
    private RotationMode currentRotationMode = RotationMode.HELIXWAVE;

    private double attackRange = 3.5;
    private boolean checkVisibility = false;
    private boolean swingArm = true;
    private boolean useCooldown = true; // Защита от промахов через шкалу перезарядки
    private boolean targetPlayers = true;
    private boolean targetMobs = false;

    private float lastYaw, lastPitch;
    private boolean isSilentAttacking = false;

    public KillAuraModule() {
        super("KillAura", "Combat");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;

        // Проверка кулдауна оружия для версии 1.21.11 (100% максимальный урон)
        if (useCooldown && mc.player.getAttackCooldownProgress(0.5f) < 0.92f) {
            return;
        }

        LivingEntity target = findTarget();
        if (target == null) {
            resetSilentRotations();
            return;
        }

        applyRotation(target);

        double dist = mc.player.distanceTo(target);
        if (dist <= attackRange) {
            mc.interactionManager.attackEntity(mc.player, target);
            if (swingArm) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }

        resetSilentRotations();
    }

    private void applyRotation(LivingEntity target) {
        if (currentRotationMode == RotationMode.NONE) return;

        if (currentRotationMode == RotationMode.SILENT) {
            if (!isSilentAttacking) {
                lastYaw = mc.player.getYaw();
                lastPitch = mc.player.getPitch();
                isSilentAttacking = true;
            }
            rotateToTarget(target, false);
            return;
        }

        switch (currentRotationMode) {
            case HELIXWAVE -> rotateHelixWave(target);
            case NORMAL -> rotateToTarget(target, true);
            case RANDOM -> {
                rotateToTarget(target, true);
                float randomYawShift = (random.nextFloat() - 0.5f) * 0.8f;
                float randomPitchShift = (random.nextFloat() - 0.5f) * 0.8f;
                mc.player.setYaw(mc.player.getYaw() + randomYawShift);
                mc.player.setPitch(mc.player.getPitch() + randomPitchShift);
            }
            case SWITCH -> rotateToTarget(target, false);
            default -> {}
        }
    }

    private void resetSilentRotations() {
        if (currentRotationMode == RotationMode.SILENT && isSilentAttacking) {
            mc.player.setYaw(lastYaw);
            mc.player.setPitch(lastPitch);
            isSilentAttacking = false;
        }
    }

    private void rotateHelixWave(Entity target) {
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d diff = targetPos.subtract(eyePos);

        float targetYaw = (float) (MathHelper.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90f;
        float targetPitch = (float) (-MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0 / Math.PI);

        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        float diffYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float diffPitch = targetPitch - currentPitch;

        // Микроколебания HelixWave строго в пределах хитбокса цели
        double now = System.currentTimeMillis() / 1000.0;
        float helixYaw = (float) (Math.sin(now * 12.0) * 1.8 + Math.cos(now * 6.0) * 0.8);
        float helixPitch = (float) (Math.cos(now * 12.0) * 1.2 + Math.sin(now * 6.0) * 0.5);

        float finalYaw = currentYaw + MathHelper.clamp(diffYaw * 0.75f + helixYaw, -40f, 40f);
        float finalPitch = currentPitch + MathHelper.clamp(diffPitch * 0.75f + helixPitch, -20f, 20f);

        mc.player.setYaw(MathHelper.wrapDegrees(finalYaw));
        mc.player.setPitch(MathHelper.clamp(finalPitch, -89.9f, 89.9f));
    }

    private void rotateToTarget(Entity target, boolean smooth) {
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d diff = targetPos.subtract(eyePos);

        float targetYaw = (float) (MathHelper.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90f;
        float targetPitch = (float) (-MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0 / Math.PI);

        if (smooth) {
            float curYaw = mc.player.getYaw();
            float curPitch = mc.player.getPitch();
            float dYaw = MathHelper.wrapDegrees(targetYaw - curYaw);
            float dPitch = targetPitch - curPitch;

            mc.player.setYaw(curYaw + MathHelper.clamp(dYaw * 0.6f, -30f, 30f));
            mc.player.setPitch(curPitch + MathHelper.clamp(dPitch * 0.6f, -15f, 15f));
        } else {
            mc.player.setYaw(MathHelper.wrapDegrees(targetYaw));
            mc.player.setPitch(MathHelper.clamp(targetPitch, -89.9f, 89.9f));
        }
    }

    private LivingEntity findTarget() {
        Box box = mc.player.getBoundingBox().expand(attackRange);
        List<Entity> list = mc.world.getOtherEntities(mc.player, box, e -> {
            if (e == mc.player || !(e instanceof LivingEntity living)) return false;
            if (!living.isAlive() || living.getHealth() <= 0) return false;
            if (e.isSpectator()) return false;

            if (e instanceof PlayerEntity) return targetPlayers;
            return targetMobs;
        });

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : list) {
            LivingEntity living = (LivingEntity) e;
            double d = mc.player.distanceTo(living);

            if (d <= attackRange && d < bestDist) {
                if (checkVisibility && !mc.player.canSee(living)) continue;
                best = living;
                bestDist = d;
            }
        }
        return best;
    }

    public void setAttackRange(double range) { this.attackRange = Math.max(1, Math.min(6, range)); }
    public void setRotationMode(RotationMode mode) { this.currentRotationMode = mode; }
}

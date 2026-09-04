package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

import java.util.Random;
import java.util.List;

public class KillAuraModule extends Module {
    private final Random random = new Random();
    private long lastAttack = 0;
    private long nextDelay = 0;

    // Добавлен новый режим ротации HELIXWAVE
    public enum RotationMode { NONE, NORMAL, SILENT, SWITCH, RANDOM, HELIXWAVE }
    private RotationMode currentRotationMode = RotationMode.HELIXWAVE; // Сразу ставим его по умолчанию

    private double attackRange = 2.95;
    private float fov = 45f;
    private boolean checkVisibility = false; // Отключаем, чтобы бить инвизок сквозь стены
    private boolean attackOnlyWhenLooking = false;
    private boolean swingArm = true;
    private boolean randomizeDelay = true;
    private long minDelay = 180;
    private long maxDelay = 320;
    private boolean targetPlayers = true;
    private boolean targetMobs = false;

    private float rotationSpeed = 0.2f;
    private float yawOffset = 0.2f;
    private float pitchOffset = 0.1f;

    private float lastYaw, lastPitch;
    private boolean isSilentAttacking = false;

    public KillAuraModule() {
        super("KillAura", "Combat");
        updateNextDelay();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextDelay) {
            if (currentRotationMode == RotationMode.SILENT && isSilentAttacking) {
                mc.player.setYaw(lastYaw);
                mc.player.setPitch(lastPitch);
                isSilentAttacking = false;
            }
            return;
        }

        Entity target = findTarget();
        if (target == null) {
            if (currentRotationMode == RotationMode.SILENT && isSilentAttacking) {
                mc.player.setYaw(lastYaw);
                mc.player.setPitch(lastPitch);
                isSilentAttacking = false;
            }
            return;
        }

        // Логика ротаций, включая HelixWave для обхода античита Shard
        if (currentRotationMode == RotationMode.HELIXWAVE) {
            rotateHelixWave(target);
        } else {
            switch (currentRotationMode) {
                case NONE: break;
                case NORMAL: rotateToTarget(target, true); break;
                case SILENT:
                    if (!isSilentAttacking) {
                        lastYaw = mc.player.getYaw();
                        lastPitch = mc.player.getPitch();
                        isSilentAttacking = true;
                    }
                    rotateToTarget(target, false);
                    break;
                case SWITCH: rotateToTarget(target, true); break;
                case RANDOM:
                    float randomYawShift = (random.nextFloat() - 0.5f) * 0.4f;
                    float randomPitchShift = (random.nextFloat() - 0.5f) * 0.4f;
                    rotateToTarget(target, true);
                    mc.player.setYaw(mc.player.getYaw() + randomYawShift);
                    mc.player.setPitch(mc.player.getPitch() + randomPitchShift);
                    break;
            }
        }

        double dist = mc.player.distanceTo(target);
        if (dist <= 3.0 && dist <= attackRange) {
            mc.interactionManager.attackEntity(mc.player, target);
            if (swingArm) mc.player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
            updateNextDelay();
        }

        if (currentRotationMode == RotationMode.SILENT) {
            mc.player.setYaw(lastYaw);
            mc.player.setPitch(lastPitch);
            isSilentAttacking = false;
        }
    }

    // Тот самый обход HelixWave, переведённый с Lua на Java
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

        // Базовая доводка (множитель 0.68 из скрипта Lua)
        float baseYaw = currentYaw + (diffYaw * 0.68f);
        float basePitch = currentPitch + (diffPitch * 0.68f);

        // Генерация спиральной волны синусов и косинусов от времени
        double now = System.currentTimeMillis() / 1000.0; // перевод в секунды
        float helixYaw = (float) (Math.sin(now * 18.0) * 14.0 + Math.cos(now * 9.0) * 6.0);
        float helixPitch = (float) (Math.cos(now * 18.0) * 10.0 + Math.sin(now * 9.0) * 5.0);

        // Применяем и сглаживаем углы
        mc.player.setYaw(MathHelper.wrapDegrees(baseYaw + helixYaw));
        mc.player.setPitch(MathHelper.clamp(basePitch + helixPitch, -89.9f, 89.9f));
    }

    private void rotateToTarget(Entity target, boolean smooth) {
        if (mc.player == null) return;
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d diff = targetPos.subtract(eyePos);

        float targetYaw = (float) (MathHelper.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90f;
        float targetPitch = (float) (-MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0 / Math.PI);

        targetYaw = MathHelper.wrapDegrees(targetYaw);
        targetPitch = MathHelper.wrapDegrees(targetPitch);

        if (smooth) {
            float curYaw = mc.player.getYaw();
            float curPitch = mc.player.getPitch();
            float dYaw = MathHelper.wrapDegrees(targetYaw - curYaw);
            float dPitch = targetPitch - curPitch;
            float speed = rotationSpeed * 5f;
            mc.player.setYaw(curYaw + MathHelper.clamp(dYaw * speed, -1.5f, 1.5f));
            mc.player.setPitch(curPitch + MathHelper.clamp(dPitch * speed, -0.8f, 0.8f));
        } else {
            mc.player.setYaw(targetYaw);
            mc.player.setPitch(targetPitch);
        }
    }

    private Entity findTarget() {
        Box box = mc.player.getBoundingBox().expand(3.0);
        List<Entity> list = mc.world.getOtherEntities(mc.player, box, e -> {
            if (e == mc.player) return false;
            if (!(e instanceof LivingEntity)) return false;
            if (e instanceof PlayerEntity) return targetPlayers;
            else return targetMobs;
        });
        if (list.isEmpty()) return null;
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : list) {
            double d = mc.player.distanceTo(e);
            if (d < bestDist && d <= attackRange && d <= 3.0) {
                if (checkVisibility && !mc.player.canSee(e)) continue;
                best = e;
                bestDist = d;
            }
        }
        return best;
    }

    private void updateNextDelay() {
        if (randomizeDelay) {
            nextDelay = minDelay + random.nextInt((int)(maxDelay - minDelay + 1));
        } else {
            nextDelay = minDelay;
        }
    }

    public void setAttackRange(double range) { this.attackRange = Math.max(1, Math.min(6, range)); }
    public void setRotationMode(RotationMode mode) { this.currentRotationMode = mode; }
}

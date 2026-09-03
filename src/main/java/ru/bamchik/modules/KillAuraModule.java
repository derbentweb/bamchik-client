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

    public enum RotationMode { NONE, NORMAL, SILENT, SWITCH, RANDOM }
    private RotationMode currentRotationMode = RotationMode.NORMAL;

    private double attackRange = 2.95;
    private float fov = 45f;
    private boolean checkVisibility = true;
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
    private float randomYawShift, randomPitchShift;

    public KillAuraModule() {
        super("KillAura");
        updateNextDelay();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
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
                randomYawShift = (random.nextFloat() - 0.5f) * 0.4f;
                randomPitchShift = (random.nextFloat() - 0.5f) * 0.4f;
                rotateToTarget(target, true);
                mc.player.setYaw(mc.player.getYaw() + randomYawShift);
                mc.player.setPitch(mc.player.getPitch() + randomPitchShift);
                break;
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

    private void rotateToTarget(Entity target, boolean smooth) {
        if (mc.player == null) return;
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d diff = targetPos.subtract(eyePos);

        float targetYaw = (float) (MathHelper.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90f;
        float targetPitch = (float) (-MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0 / Math.PI);

        if (yawOffset != 0) targetYaw += (random.nextFloat() - 0.5f) * yawOffset * 2;
        if (pitchOffset != 0) targetPitch += (random.nextFloat() - 0.5f) * pitchOffset * 2;

        targetYaw = MathHelper.wrapDegrees(targetYaw);
        targetPitch = MathHelper.wrapDegrees(targetPitch);

        if (smooth) {
            float curYaw = mc.player.getYaw();
            float curPitch = mc.player.getPitch();
            float dYaw = MathHelper.wrapDegrees(targetYaw - curYaw);
            float dPitch = targetPitch - curPitch;
            float speed = rotationSpeed * 5f;
            if (Math.abs(dYaw) > 1f) {
                mc.player.setYaw(curYaw + MathHelper.clamp(dYaw * speed, -1.5f, 1.5f));
            } else {
                mc.player.setYaw(targetYaw);
            }
            if (Math.abs(dPitch) > 0.5f) {
                mc.player.setPitch(curPitch + MathHelper.clamp(dPitch * speed, -0.8f, 0.8f));
            } else {
                mc.player.setPitch(targetPitch);
            }
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
                if (attackOnlyWhenLooking && !isInFov(e)) continue;
                best = e;
                bestDist = d;
            }
        }
        return best;
    }

    private boolean isInFov(Entity target) {
        if (mc.player == null) return false;
        Vec3d eye = mc.player.getEyePos();
        Vec3d toTarget = target.getBoundingBox().getCenter().subtract(eye).normalize();
        Vec3d lookVec = mc.player.getRotationVector();
        double angle = Math.acos(lookVec.dotProduct(toTarget));
        return Math.toDegrees(angle) <= fov;
    }

    private void updateNextDelay() {
        if (randomizeDelay) {
            nextDelay = minDelay + random.nextInt((int)(maxDelay - minDelay + 1));
        } else {
            nextDelay = minDelay;
        }
    }

    // Сеттеры для GUI и конфига
    public void setAttackRange(double range) { this.attackRange = Math.max(1, Math.min(6, range)); }
    public void setFov(float fov) { this.fov = Math.max(10, Math.min(180, fov)); }
    public void setMinDelay(long min) { this.minDelay = min; updateNextDelay(); }
    public void setMaxDelay(long max) { this.maxDelay = max; updateNextDelay(); }
    public void setSwingArm(boolean val) { this.swingArm = val; }
    public void setCheckVisibility(boolean val) { this.checkVisibility = val; }
    public void setRotationMode(RotationMode mode) { this.currentRotationMode = mode; }
    public double getAttackRange() { return attackRange; }
    public float getFov() { return fov; }
}
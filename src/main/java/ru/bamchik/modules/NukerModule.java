package ru.bamchik.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.bamchik.Module;

public class NukerModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final int radius = 4;

    public NukerModule() {
        super("Nuker", "Автоматически ломает блоки вокруг игрока");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos playerPos = mc.player.getBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(targetPos).getBlock();

                    if (block != Blocks.AIR) {
                        // Исправлено вычисление расстояния до центра блока под Minecraft 1.21.1
                        if (mc.player.getPos().distanceTo(Vec3d.ofCenter(targetPos)) <= radius) {
                            mc.interactionManager.updateBlockBreakingProgress(targetPos, net.minecraft.util.math.Direction.UP);
                            mc.player.swingHand(net.minecraft.util.hand.Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}

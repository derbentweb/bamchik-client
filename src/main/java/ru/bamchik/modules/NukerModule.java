package ru.bamchik.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand; // Исправлен импорт руки
import ru.bamchik.Module;

public class NukerModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final int radius = 4;

    public NukerModule() {
        // Конструктор изменен под ваш личный класс Module (передаем только имя чита)
        super("Nuker"); 
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
                        if (mc.player.getPos().distanceTo(Vec3d.ofCenter(targetPos)) <= radius) {
                            mc.interactionManager.updateBlockBreakingProgress(targetPos, net.minecraft.util.math.Direction.UP);
                            mc.player.swingHand(Hand.MAIN_HAND); // Исправлен вызов маха рукой
                        }
                    }
                }
            }
        }
    }
}

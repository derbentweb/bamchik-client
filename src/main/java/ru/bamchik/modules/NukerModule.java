package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NukerModule extends Module {
    public NukerModule() { super("Nuker"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        int r = 5;
        BlockPos pos = mc.player.getBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    Block b = mc.world.getBlockState(target).getBlock();
                    if (b != Blocks.AIR && mc.player.distanceTo(Vec3d.ofCenter(target)) <= r) {
                        mc.interactionManager.breakBlock(target);
                    }
                }
            }
        }
    }
}
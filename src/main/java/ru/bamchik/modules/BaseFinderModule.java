package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BaseFinderModule extends Module {
    private static final Set<Block> BASE_BLOCKS = new HashSet<>(Arrays.asList(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL, Blocks.SHULKER_BOX,
            Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CRAFTING_TABLE,
            Blocks.ENCHANTING_TABLE, Blocks.ANVIL, Blocks.GRINDSTONE, Blocks.STONECUTTER,
            Blocks.BED, Blocks.RESPAWN_ANCHOR, Blocks.BREWING_STAND, Blocks.CAULDRON,
            Blocks.LECTERN, Blocks.LOOM, Blocks.CARTOGRAPHY_TABLE, Blocks.FLETCHING_TABLE,
            Blocks.SMITHING_TABLE, Blocks.COMPOSTER
    ));

    private int scanRadius = 60;
    private int scanDelay = 20;
    private int tickCounter = 0;
    private Map<BlockPos, Block> foundBases = new ConcurrentHashMap<>();
    private boolean showInChat = true;
    private boolean showMarkers = true;
    private int markerColor = 0xFF00FF00;

    public BaseFinderModule() { super("BaseFinder"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        tickCounter++;
        if (tickCounter < scanDelay) return;
        tickCounter = 0;

        BlockPos playerPos = mc.player.getBlockPos();
        Map<BlockPos, Block> newFound = new ConcurrentHashMap<>();
        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dy = -scanRadius; dy <= scanRadius; dy++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (BASE_BLOCKS.contains(block)) {
                        newFound.put(pos, block);
                    }
                }
            }
        }
        foundBases = newFound;

        if (showInChat && !foundBases.isEmpty() && mc.player != null) {
            mc.player.sendMessage(Text.literal("§a[BaseFinder] Найдено §e" + foundBases.size() + " §aблоков."), false);
            int count = 0;
            for (Map.Entry<BlockPos, Block> entry : foundBases.entrySet()) {
                if (count++ >= 5) break;
                BlockPos p = entry.getKey();
                mc.player.sendMessage(Text.literal("§7 - " + entry.getValue().getName().getString() + " §fX:" + p.getX() + " Y:" + p.getY() + " Z:" + p.getZ()), false);
            }
            if (foundBases.size() > 5)
                mc.player.sendMessage(Text.literal("§7... и ещё " + (foundBases.size() - 5) + " блоков."), false);
        }
    }

    public void setScanRadius(int r) { this.scanRadius = Math.max(10, Math.min(200, r)); }
    public void setScanDelay(int d) { this.scanDelay = Math.max(5, d); }
    // остальные сеттеры
}
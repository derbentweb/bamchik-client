package ru.bamchik.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import java.util.HashSet;
import java.util.Set;
import ru.bamchik.Module;

public class XRayModule extends Module {
    public static final Set<Block> ORE_BLOCKS = new HashSet<>();

    public XRayModule() {
        super("XRay");
        
        // Добавляем актуальные блоки под Minecraft 1.21.1
        ORE_BLOCKS.add(Blocks.DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.GOLD_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        ORE_BLOCKS.add(Blocks.IRON_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        ORE_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        ORE_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        ORE_BLOCKS.add(Blocks.SPAWNER); // Исправлено: вместо MOB_SPAWNER теперь просто SPAWNER
        ORE_BLOCKS.add(Blocks.CHEST);
    }
}

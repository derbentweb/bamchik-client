package ru.bamchik.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import java.util.HashSet;
import java.util.Set;
import ru.bamchik.Module;

public class BaseFinderModule extends Module {
    public static final Set<Block> BASE_BLOCKS = new HashSet<>();

    public BaseFinderModule() {
        super("BaseFinder");
        
        BASE_BLOCKS.add(Blocks.RED_BED);
        BASE_BLOCKS.add(Blocks.WHITE_BED);
        BASE_BLOCKS.add(Blocks.RESPAWN_ANCHOR);
        BASE_BLOCKS.add(Blocks.BREWING_STAND);
        BASE_BLOCKS.add(Blocks.CAULDRON);
        BASE_BLOCKS.add(Blocks.CHEST);
        BASE_BLOCKS.add(Blocks.ENDER_CHEST);
    }

    @Override
    public void onTick() {
        // Пустой метод, обязательный для компиляции
    }
}

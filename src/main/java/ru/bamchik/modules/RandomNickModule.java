package ru.bamchik.modules;

import ru.bamchik.Module;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

public class RandomNickModule extends Module {
    private final Random random = new Random();
    private static final String[] PREFIXES = {"Player", "Creeper", "Skeleton", "Zombie", "Enderman", "Wither", "Blaze", "Slime", "Ghast", "Piglin"};
    private static final String[] SUFFIXES = {"X", "Z", "K", "Q", "V", "M", "L", "R", "T", "F"};

    public RandomNickModule() { super("RandomNick", "Misc"); }

    @Override public void onTick() {}

    public static String generateRandomNick() {
        Random rand = new Random();
        String prefix = PREFIXES[rand.nextInt(PREFIXES.length)];
        String suffix = SUFFIXES[rand.nextInt(SUFFIXES.length)];
        int number = rand.nextInt(9000) + 1000;
        return prefix + suffix + number;
    }

    public void execute() {
        String nick = generateRandomNick();
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§a[RandomNick] Сгенерирован ник: §e" + nick), false);
        }
        try {
            StringSelection selection = new StringSelection(nick);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("§7(Ник скопирован в буфер обмена)"), false);
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("§cНе удалось скопировать ник"), false);
            }
        }
    }
}

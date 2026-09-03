package ru.bamchik.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
    private static final Pattern BIND = Pattern.compile("^\\.bind\\s+(\\w+)\\s+(\\w+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UNBIND = Pattern.compile("^\\.unbind\\s+(\\w+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BINDS = Pattern.compile("^\\.binds$", Pattern.CASE_INSENSITIVE);

    public static boolean handleCommand(String msg) {
        if (!msg.startsWith(".")) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;

        Matcher m;
        m = BIND.matcher(msg);
        if (m.matches()) {
            String modName = m.group(1);
            String keyName = m.group(2).toUpperCase();
            Module module = BamchikClient.getInstance().getModuleManager().getModuleByName(modName);
            if (module == null) {
                mc.player.sendMessage(Text.literal("§cМодуль не найден."), false);
                return true;
            }
            int code = KeyBindHelper.getKeyCode(keyName);
            if (code == -1) {
                mc.player.sendMessage(Text.literal("§cНеизвестная клавиша."), false);
                return true;
            }
            module.setKeyBind(code);
            mc.player.sendMessage(Text.literal("§aБинд для §e" + modName + " §aустановлен на §e" + keyName), false);
            ConfigManager.saveConfig();
            return true;
        }

        m = UNBIND.matcher(msg);
        if (m.matches()) {
            String modName = m.group(1);
            Module module = BamchikClient.getInstance().getModuleManager().getModuleByName(modName);
            if (module == null) {
                mc.player.sendMessage(Text.literal("§cМодуль не найден."), false);
                return true;
            }
            module.setKeyBind(0);
            mc.player.sendMessage(Text.literal("§aБинд для §e" + modName + " §aудалён."), false);
            ConfigManager.saveConfig();
            return true;
        }

        m = BINDS.matcher(msg);
        if (m.matches()) {
            StringBuilder sb = new StringBuilder("§6Список биндов:\n");
            boolean has = false;
            for (Module mod : BamchikClient.getInstance().getModuleManager().getModules()) {
                if (mod.getKeyBind() != 0) {
                    String kn = KeyBindHelper.getKeyName(mod.getKeyBind());
                    sb.append("§7 - §f").append(mod.getName()).append(" §7→ §e").append(kn).append("\n");
                    has = true;
                }
            }
            if (!has) sb.append("§7Нет активных биндов.");
            mc.player.sendMessage(Text.literal(sb.toString()), false);
            return true;
        }

        return false;
    }
}
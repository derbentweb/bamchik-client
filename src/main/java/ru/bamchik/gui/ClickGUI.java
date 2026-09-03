package ru.bamchik.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.modules.*;
import ru.bamchik.utils.ConfigManager;

public class ClickGUI extends Screen {
    public static int guiColor = 0xFF00AAFF;
    public static int textColor = 0xFFFFFFFF;
    public static int bgColor = 0xCC000000;
    public static float scale = 1.0f;
    public static String theme = "dark";

    public ClickGUI() { super(Text.literal("bamchik client")); }

    @Override
    protected void init() {
        super.init();
        applyTheme();
        int y = 30;
        int x = 20;
        int bw = (int)(120 * scale);
        int bh = (int)(20 * scale);

        for (Module module : BamchikClient.getInstance().getModuleManager().getModules()) {
            String label = module.getName() + (module.isEnabled() ? " §a✔" : " §c✖");
            ButtonWidget btn = ButtonWidget.builder(Text.literal(label), b -> {
                module.setEnabled(!module.isEnabled());
                b.setMessage(Text.literal(module.getName() + (module.isEnabled() ? " §a✔" : " §c✖")));
                if (module.getName().equals("RandomNick") && module.isEnabled())
                    ((RandomNickModule) module).execute();
            }).dimensions(x, y, bw, bh).build();
            this.addDrawableChild(btn);
            y += bh + 5;
        }

        // Темы
        int themeX = this.width - 130;
        int themeY = 30;
        for (String t : new String[]{"dark", "light", "neon"}) {
            ButtonWidget tb = ButtonWidget.builder(Text.literal(t), b -> {
                theme = t;
                this.clearChildren();
                this.init();
            }).dimensions(themeX, themeY, 60, 20).build();
            this.addDrawableChild(tb);
            themeY += 25;
        }

        // Слайдер масштаба
        SliderWidget scaleSlider = new SliderWidget(10, this.height - 40, 100, 20, Text.literal("Scale: " + scale), 0.5, 1.5) {
            @Override protected void updateMessage() { this.setMessage(Text.literal("Scale: " + String.format("%.2f", scale))); }
            @Override protected void applyValue() { scale = (float) this.value; ClickGUI.this.clearChildren(); ClickGUI.this.init(); }
        };
        this.addDrawableChild(scaleSlider);

        // Кнопка закрытия
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Закрыть"), b -> this.close())
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build();
        this.addDrawableChild(closeBtn);
    }

    private void applyTheme() {
        switch (theme) {
            case "dark": guiColor = 0xFF00AAFF; textColor = 0xFFFFFFFF; bgColor = 0xCC000000; break;
            case "light": guiColor = 0xFFFFAA00; textColor = 0xFF000000; bgColor = 0xCCFFFFFF; break;
            case "neon": guiColor = 0xFFFF00FF; textColor = 0xFF00FF00; bgColor = 0xCC000000; break;
            default: break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, bgColor);
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, Text.literal("bamchik client v2.0"), this.width / 2 - 50, 5, textColor);
        context.drawTextWithShadow(textRenderer, Text.literal("RShift для открытия"), this.width - 150, 5, textColor);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void close() {
        ConfigManager.saveConfig();
        super.close();
    }
}
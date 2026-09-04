package ru.bamchik.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.modules.RandomNickModule;
import ru.bamchik.utils.ConfigManager;

import java.util.List;

public class ClickGUI extends Screen {
    private int selectedCategoryIndex = 0;
    private List<String> categories;
    private int scrollY = 0;
    private static final int MODULE_BUTTON_HEIGHT = 22;
    private static final int CATEGORY_BUTTON_WIDTH = 80;

    // Цвета
    private static final int BG_COLOR = 0xCC1A1A1A;
    private static final int CATEGORY_BG = 0xFF2D2D2D;
    private static final int CATEGORY_SELECTED = 0xFF3A8CFF;
    private static final int CATEGORY_TEXT = 0xFFFFFFFF;
    private static final int MODULE_OFF = 0xFF444444;
    private static final int MODULE_ON = 0xFF3A8CFF;
    private static final int MODULE_TEXT = 0xFFFFFFFF;

    public static float scale = 1.0f; // масштаб

    public ClickGUI() {
        super(Text.literal("bamchik client"));
        categories = BamchikClient.getInstance().getModuleManager().getCategories();
        if (categories.isEmpty()) categories.add("Modules");
    }

    @Override
    protected void init() {
        super.init();

        // Кнопки категорий
        int catX = 20;
        int catY = 20;
        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            final int index = i;
            ButtonWidget catBtn = ButtonWidget.builder(Text.literal(cat), b -> {
                selectedCategoryIndex = index;
                scrollY = 0;
                this.clearChildren();
                this.init();
            }).dimensions(catX, catY, CATEGORY_BUTTON_WIDTH, 20).build();
            catBtn.active = (selectedCategoryIndex != i);
            this.addDrawableChild(catBtn);
            catX += CATEGORY_BUTTON_WIDTH + 6;
        }

        // Кнопки модулей для выбранной категории
        String currentCat = categories.get(selectedCategoryIndex);
        List<Module> modules = BamchikClient.getInstance().getModuleManager().getModulesByCategory(currentCat);
        int modX = 20;
        int modY = 60 + scrollY;
        int modWidth = 140;
        for (Module module : modules) {
            String label = module.getName() + (module.isEnabled() ? " §aON" : " §cOFF");
            ButtonWidget modBtn = ButtonWidget.builder(Text.literal(label), b -> {
                module.setEnabled(!module.isEnabled());
                b.setMessage(Text.literal(module.getName() + (module.isEnabled() ? " §aON" : " §cOFF")));
                if (module.getName().equals("RandomNick") && module.isEnabled()) {
                    ((RandomNickModule) module).execute();
                }
            }).dimensions(modX, modY, modWidth, MODULE_BUTTON_HEIGHT).build();
            this.addDrawableChild(modBtn);
            modY += MODULE_BUTTON_HEIGHT + 4;
        }

        // Кнопки управления масштабом (+ и -)
        ButtonWidget minusBtn = ButtonWidget.builder(Text.literal("-"), b -> {
            scale = Math.max(0.5f, scale - 0.1f);
            this.clearChildren();
            this.init();
        }).dimensions(this.width - 150, this.height - 30, 30, 20).build();
        this.addDrawableChild(minusBtn);

        ButtonWidget plusBtn = ButtonWidget.builder(Text.literal("+"), b -> {
            scale = Math.min(1.5f, scale + 0.1f);
            this.clearChildren();
            this.init();
        }).dimensions(this.width - 110, this.height - 30, 30, 20).build();
        this.addDrawableChild(plusBtn);

        // Кнопка закрытия
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Закрыть"), b -> this.close())
                .dimensions(this.width - 80, 5, 60, 20).build();
        this.addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, BG_COLOR);
        context.drawText(this.textRenderer, "bamchik client v2.0", 20, 5, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, "Scale: " + String.format("%.1f", scale), this.width - 180, this.height - 28, 0xFFAAAAAA, false);
        context.drawText(this.textRenderer, "RShift для закрытия", this.width - 200, 5, 0xFFAAAAAA, false);
        context.fill(20, 42, this.width - 20, 43, 0xFF555555);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        ConfigManager.saveConfig();
        super.close();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollY += (int)(verticalAmount * -10);
        if (scrollY > 0) scrollY = 0;
        int maxScroll = -(BamchikClient.getInstance().getModuleManager().getModulesByCategory(categories.get(selectedCategoryIndex)).size() * (MODULE_BUTTON_HEIGHT + 4) - (this.height - 100));
        if (scrollY < maxScroll) scrollY = maxScroll;
        this.clearChildren();
        this.init();
        return true;
    }
}

package ru.bamchik.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.modules.RandomNickModule;
import ru.bamchik.utils.ConfigManager;

import java.util.List;

public class ClickGUI extends Screen {
    private int selectedCategoryIndex = 0;
    private final List<String> categories;
    private int scrollY = 0;

    // Палитра оформления
    private static final int COLOR_OVERLAY = 0x90000000;
    private static final int COLOR_PANEL_BG = 0xF1181820;
    private static final int COLOR_SIDEBAR = 0xF1121218;
    private static final int COLOR_HEADER = 0xFF2A2A38;
    private static final int COLOR_ACCENT = 0xFF3A8CFF;
    private static final int COLOR_ACCENT_HOVER = 0xFF559CFF;
    private static final int COLOR_MODULE_OFF = 0xFF22222E;
    private static final int COLOR_MODULE_ON = 0xFF1E3A5F;
    private static final int COLOR_BORDER = 0xFF333344;
    private static final int COLOR_TEXT_MUTED = 0xFFAAAAAA;

    public static float scale = 1.0f;

    public ClickGUI() {
        super(Text.literal("bamchik client"));
        this.categories = BamchikClient.getInstance().getModuleManager().getCategories();
        if (this.categories.isEmpty()) {
            this.categories.add("Modules");
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        context.fill(0, 0, this.width, this.height, COLOR_OVERLAY);

        // Размеры и позиционирование главного окна
        int guiWidth = 460;
        int guiHeight = 280;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        // Фон главного окна и рамка
        context.fill(x, y, x + guiWidth, y + guiHeight, COLOR_PANEL_BG);
        context.drawBorder(x, y, guiWidth, guiHeight, COLOR_BORDER);

        // Верхняя шапка
        int headerHeight = 30;
        context.fill(x, y, x + guiWidth, y + headerHeight, COLOR_HEADER);
        context.drawText(this.textRenderer, "BAMCHIK CLIENT v2.0", x + 12, y + 10, 0xFFFFFFFF, true);
        
        String hintText = "RShift / ESC — Закрыть";
        int hintWidth = this.textRenderer.getWidth(hintText);
        context.drawText(this.textRenderer, hintText, x + guiWidth - hintWidth - 12, y + 10, COLOR_TEXT_MUTED, false);

        // Боковая панель категорий
        int sidebarWidth = 110;
        int contentY = y + headerHeight;
        int contentHeight = guiHeight - headerHeight;
        context.fill(x, contentY, x + sidebarWidth, y + guiHeight, COLOR_SIDEBAR);
        context.fill(x + sidebarWidth, contentY, x + sidebarWidth + 1, y + guiHeight, COLOR_BORDER);

        // Отрисовка вкладок категорий
        int catY = contentY + 10;
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            boolean isSelected = (i == selectedCategoryIndex);
            boolean isHovered = mouseX >= x + 5 && mouseX <= x + sidebarWidth - 5 && mouseY >= catY && mouseY <= catY + 22;

            int catBg = isSelected ? COLOR_ACCENT : (isHovered ? 0x333A8CFF : 0x00000000);
            if (catBg != 0) {
                context.fill(x + 6, catY, x + sidebarWidth - 6, catY + 22, catBg);
            }

            int textColor = isSelected ? 0xFFFFFFFF : (isHovered ? 0xFFDDDDDD : COLOR_TEXT_MUTED);
            context.drawText(this.textRenderer, category, x + 16, catY + 7, textColor, isSelected);

            catY += 26;
        }

        // Область просмотра модулей (с ограничениями скролла)
        int mainX = x + sidebarWidth + 10;
        int mainWidth = guiWidth - sidebarWidth - 20;
        int moduleY = contentY + 10 + scrollY;

        String currentCat = categories.get(selectedCategoryIndex);
        List<Module> modules = BamchikClient.getInstance().getModuleManager().getModulesByCategory(currentCat);

        context.enableScissor(mainX - 5, contentY + 5, mainX + mainWidth + 5, y + guiHeight - 5);

        for (Module module : modules) {
            // Проверка видимости карточки вьюпортом
            if (moduleY + 28 >= contentY && moduleY <= y + guiHeight) {
                boolean enabled = module.isEnabled();
                boolean isHovered = mouseX >= mainX && mouseX <= mainX + mainWidth && mouseY >= moduleY && mouseY <= moduleY + 26;

                int cardBg = enabled ? (isHovered ? COLOR_ACCENT_HOVER : COLOR_MODULE_ON) : (isHovered ? 0xFF2D2D3D : COLOR_MODULE_OFF);
                context.fill(mainX, moduleY, mainX + mainWidth, moduleY + 26, cardBg);
                context.drawBorder(mainX, moduleY, mainWidth, 26, enabled ? COLOR_ACCENT : COLOR_BORDER);

                // Название модуля
                context.drawText(this.textRenderer, module.getName(), mainX + 10, moduleY + 9, 0xFFFFFFFF, true);

                // Индикатор состояния (ON/OFF)
                String status = enabled ? "ON" : "OFF";
                int statusColor = enabled ? 0xFF4ADE80 : 0xFFF87171;
                int statusX = mainX + mainWidth - this.textRenderer.getWidth(status) - 10;
                context.drawText(this.textRenderer, status, statusX, moduleY + 9, statusColor, true);
            }
            moduleY += 30;
        }

        context.disableScissor();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int guiWidth = 460;
        int guiHeight = 280;
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        int headerHeight = 30;
        int sidebarWidth = 110;
        int contentY = y + headerHeight;

        // Клик по категориям
        int catY = contentY + 10;
        for (int i = 0; i < categories.size(); i++) {
            if (mouseX >= x + 5 && mouseX <= x + sidebarWidth - 5 && mouseY >= catY && mouseY <= catY + 22) {
                selectedCategoryIndex = i;
                scrollY = 0;
                return true;
            }
            catY += 26;
        }

        // Клик по модулям
        int mainX = x + sidebarWidth + 10;
        int mainWidth = guiWidth - sidebarWidth - 20;
        int moduleY = contentY + 10 + scrollY;

        String currentCat = categories.get(selectedCategoryIndex);
        List<Module> modules = BamchikClient.getInstance().getModuleManager().getModulesByCategory(currentCat);

        for (Module module : modules) {
            if (mouseX >= mainX && mouseX <= mainX + mainWidth && mouseY >= moduleY && mouseY <= moduleY + 26) {
                if (mouseY >= contentY + 5 && mouseY <= y + guiHeight - 5) {
                    module.setEnabled(!module.isEnabled());
                    if (module.getName().equals("RandomNick") && module.isEnabled()) {
                        if (module instanceof RandomNickModule nickModule) {
                            nickModule.execute();
                        }
                    }
                    return true;
                }
            }
            moduleY += 30;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        String currentCat = categories.get(selectedCategoryIndex);
        int moduleCount = BamchikClient.getInstance().getModuleManager().getModulesByCategory(currentCat).size();

        int maxScroll = 0;
        int minScroll = Math.min(0, 240 - (moduleCount * 30 + 10));

        scrollY += (int) (verticalAmount * 18);
        if (scrollY > maxScroll) scrollY = maxScroll;
        if (scrollY < minScroll) scrollY = minScroll;

        return true;
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
}

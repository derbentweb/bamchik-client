package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addNickButton(CallbackInfo ci) {
        ButtonWidget nickButton = ButtonWidget.builder(Text.literal("Сменить ник"), button -> {
            String newNick = "Player_" + (int)(Math.random() * 8999 + 1000);
            changeNickname(newNick);
        }).dimensions(10, 10, 110, 20).build();

        this.addDrawableChild(nickButton);
    }

    private void changeNickname(String newNick) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Session newSession = new Session(
                newNick,
                UUID.randomUUID(),
                "",
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.MOJANG
            );

            // Поиск поля сессии по типу класса для защиты от смены имен в маппингах
            for (Field field : MinecraftClient.class.getDeclaredFields()) {
                if (field.getType().equals(Session.class)) {
                    field.setAccessible(true);
                    field.set(client, newSession);
                    System.out.println("[Bamchik Client] Ник изменён на: " + newNick);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

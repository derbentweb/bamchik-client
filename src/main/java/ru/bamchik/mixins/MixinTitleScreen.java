package ru.bamchik.mixins;

import net.minecraft.client.MinecraftClient;
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
public class MixinTitleScreen {

    @Inject(method = "init", at = @At("TAIL"))
    private void addNickButton(CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;

        ButtonWidget nickButton = ButtonWidget.builder(Text.literal("Сменить ник"), button -> {
            String newNick = "Player_" + (int)(Math.random() * 8999 + 1000);
            changeNickname(newNick);
        }).dimensions(10, 10, 110, 20).build();

        screen.addDrawableChild(nickButton);
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

            Field sessionField = MinecraftClient.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(client, newSession);

            System.out.println("[Bamchik Client] Ник успешно изменён на: " + newNick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

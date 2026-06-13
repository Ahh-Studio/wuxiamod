package com.aiden.wuxia.screen;

import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.SetActionC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;

public class MenuScreen extends Screen {
    public MenuScreen() {
        super(Component.translatable("wuxiaScreen.title"));
    }

    @Override
    protected void init() {
        super.init();
        Button jiangHuButton = Button.builder(Component.translatable("wuxiaScreen.jianghu"), button -> {

        }).bounds(40, 40, 400, 20).build();
        Button skillsButton = Button.builder(Component.translatable("wuxiaScreen.skills"), button -> {

        }).bounds(40, 70, 400, 20).build();
        Button attributesButton = Button.builder(Component.translatable("wuxiaScreen.attributes"), _ -> {
            this.minecraft.setScreen(new AttributesScreen(this));
        }).bounds(40, 100, 400, 20).build();

        LocalPlayer localPlayer = this.minecraft.player;
        PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) localPlayer;
        Button meditateButton = Button.builder(Component.translatable("wuxiaScreen.meditate"), _ -> {
            SetActionC2SPayload setActionC2SPayload = new SetActionC2SPayload(Action.MEDITATE.name());
            ClientPlayNetworking.send(setActionC2SPayload);
            this.minecraft.setScreen(null);
        }).bounds(40, 140, 185, 20).build();
        Button healButton = Button.builder(Component.translatable("wuxiaScreen.heal"), _ -> {
            SetActionC2SPayload setActionC2SPayload = new SetActionC2SPayload(Action.HEAL.name());
            ClientPlayNetworking.send(setActionC2SPayload);
            this.minecraft.setScreen(null);
        }).bounds(255, 140, 185, 20).build();
        Button clearActionButton = Button.builder(Component.translatable("wuxiaScreen.clear_action"), _ -> {
            SetActionC2SPayload setActionC2SPayload = new SetActionC2SPayload(Action.NONE.name());
            ClientPlayNetworking.send(setActionC2SPayload);
            this.minecraft.setScreen(null);
        }).bounds(40, 170, 400, 20).build();

        this.addRenderableWidget(jiangHuButton);
        this.addRenderableWidget(skillsButton);
        this.addRenderableWidget(attributesButton);
        this.addRenderableWidget(meditateButton);
        this.addRenderableWidget(healButton);
        this.addRenderableWidget(clearActionButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(this.font, this.title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}

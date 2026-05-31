package com.aiden.wuxia.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        Button attributesButton = Button.builder(Component.translatable("wuxiaScreen.attributes"), button -> {
            this.minecraft.setScreen(new AttributesScreen(this));
        }).bounds(40, 100, 400, 20).build();

        this.addRenderableWidget(jiangHuButton);
        this.addRenderableWidget(skillsButton);
        this.addRenderableWidget(attributesButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(this.font, this.title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}

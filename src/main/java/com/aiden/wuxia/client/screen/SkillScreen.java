package com.aiden.wuxia.client.screen;

import com.aiden.wuxia.enums.Skill;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class SkillScreen extends Screen {
    public Skill skill;
    public Screen parent;

    public SkillScreen(Screen parent, Skill skill) {
        super(Component.translatable(skill.translationKey));
        this.skill = skill;
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                _ -> this.minecraft.setScreen(this.parent)
        ).pos(this.width / 2 - 100, this.height - 35).width(200).build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(this.font, this.title.getString(), 40, 40, this.skill.rarity.color, true);
    }
}

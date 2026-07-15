package com.aiden.wuxia.client.screen;

import com.aiden.wuxia.enums.Rarity;
import com.aiden.wuxia.payloads.EquipSkillC2SPayload;
import com.aiden.wuxia.skill.Skill;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.util.SkillProperty;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Function;

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
        PlayerMixinExtension playerExt = (PlayerMixinExtension) this.minecraft.player;
        if (playerExt == null) return;

        if (this.skill.rarity != Rarity.COMMON) {
            for (int i = 0; i < this.skill.types.length; i++) {
                Skill.Type skillType = this.skill.types[i];
                Component buttonMessage = Component.translatable("skill_screen.equip_as", Component.translatable(skillType.getBasicSkill().translationKey));
                Button button = Button.builder(buttonMessage, _ -> {
                    EquipSkillC2SPayload payload = new EquipSkillC2SPayload(skillType.name(), this.skill.name());
                    ClientPlayNetworking.send(payload);
                    this.minecraft.setScreen(this.parent);
                }).bounds(10, 70 + i * 30, 80, 20).build();
                this.addRenderableWidget(button);
            }
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                _ -> this.minecraft.setScreen(this.parent)
        ).pos(this.width / 2 - 100, this.height - 35).width(200).build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        PlayerMixinExtension playerExt = (PlayerMixinExtension) this.minecraft.player;
        if (playerExt == null) return;
        graphics.text(this.font, this.title.getString(), 40, 40, this.skill.rarity.color, true);
        int lv = playerExt.wuxia$getAllSkills().get(this.skill);
        graphics.text(this.font, "Lv. " + lv, 150, 40, this.skill.rarity.color, true);

        graphics.text(this.font, this.skill.desc, 40, 50, 0xFFFFFFFF, true);
        SkillProperty[] skillProperties = this.skill.properties;
        int x = 100, y = 70;

        for (int i = 0; i < skillProperties.length; i++) {
            SkillProperty skillProperty = skillProperties[i];
            Map<String, Function<Integer, Integer>> fieldsMap = skillProperty.getIntFieldMap();
            Skill.Type type = this.skill.types[i];
            if (type.getBasicSkill() != this.skill) {
                graphics.text(this.font, Component.translatable("skill_screen.when_equipped", Component.translatable(type.getBasicSkill().translationKey)), x, y, this.skill.rarity.color, true);
                y += 10;
            }

            for (Map.Entry<String, Function<Integer, Integer>> entry : fieldsMap.entrySet()) {
                int value = entry.getValue().apply(playerExt.wuxia$getAllSkills().get(this.skill));
                if (value != 0) {
                    Component text = Component.translatable("skill_screen." + entry.getKey()).append(": " + value);
                    graphics.text(this.font, text, x, y, this.skill.rarity.color, true);
                    y += 10;
                }
            }
            y += 10;
            if (y > 400) {
                y = 70;
                x += 150;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

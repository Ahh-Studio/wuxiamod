package com.aiden.wuxia.client.screen;

import com.aiden.wuxia.enums.Skill;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SkillsScreen extends Screen {
    private SkillsList skillsList;
    public final Screen parent;

    public SkillsScreen(Screen parent) {
        super(Component.translatable("wuxiaScreen.skills"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int listTop = 60;
        int listHeight = this.height - listTop - 40;
        this.skillsList = new SkillsList(this.minecraft, this.width, listHeight, listTop, 22);
        this.addRenderableWidget(this.skillsList);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                _ -> this.minecraft.setScreen(this.parent)
        ).pos(this.width / 2 - 100, this.height - 35).width(200).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(this.font, this.title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    protected void repositionElements() {
        this.clearWidgets();
        super.repositionElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private class SkillsList extends ObjectSelectionList<SkillsList.Entry> {
        public SkillsList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            LocalPlayer localPlayer = SkillsScreen.this.minecraft.player;
            PlayerMixinExtension playerExt = (PlayerMixinExtension) localPlayer;
            if (playerExt != null) {
                Skill[] allSkills = Skill.values();
                ArrayList<Skill> ownedSkills = new ArrayList<>();

                for (Skill skill : allSkills) {
                    if (playerExt.wuxia$getAllSkills().get(skill) > 0) {
                        ownedSkills.add(skill);
                    }
                }
                ownedSkills.sort(Comparator.comparingInt(o -> o.rarity.sortNum));

                for (Skill skill : ownedSkills) {
                    this.addEntry(new Entry(skill));
                }
            }
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        @Override
        protected void extractListBackground(final GuiGraphicsExtractor graphics) {
        }

        @Override
        protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Skill skill;
            private final Component skillDisplay;
            private final String levelText;

            private Entry(final Skill skill) {
                super();
                this.skill = skill;
                this.skillDisplay = Component.translatable(this.skill.translationKey);
                this.levelText = this.getValueText();
            }

            @Override
            public @NonNull Component getNarration() {
                return skillDisplay;
            }

            private String getValueText() {
                LocalPlayer localPlayer = SkillsScreen.this.minecraft.player;
                PlayerMixinExtension playerExt = (PlayerMixinExtension) localPlayer;
                if (playerExt != null) {
                    Map<Skill, Integer> skills = playerExt.wuxia$getAllSkills();
                    int value = skills.get(this.skill);
                    return value + "";
                }

                return "?";
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                SkillsScreen.this.minecraft.setScreen(new SkillScreen(SkillsScreen.this, this.skill));
                return super.mouseClicked(event, doubleClick);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int y = this.getContentYMiddle() - 9 / 2;
                graphics.text(SkillsScreen.this.font, this.skillDisplay, this.getContentX() + 2, y, this.skill.rarity.color);
                graphics.text(SkillsScreen.this.font, this.levelText, this.getContentRight() - SkillsScreen.this.font.width(this.levelText) - 4, y, 0xFFFFFFFF);
            }
        }
    }
}

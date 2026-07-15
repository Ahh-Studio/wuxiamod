package com.aiden.wuxia.client.screen;

import com.aiden.wuxia.enums.Rarity;
import com.aiden.wuxia.skill.Skill;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.EquipSkillC2SPayload;
import com.aiden.wuxia.skill.Skills;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public void refreshSkillsList() {
        if (this.skillsList != null) {
            this.skillsList.refreshEntries();
        }
    }

    private class SkillsList extends ObjectSelectionList<SkillsList.Entry> {
        public SkillsList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            this.refreshEntries();
        }

        public void refreshEntries() {
            this.clearEntries();
            LocalPlayer localPlayer = SkillsScreen.this.minecraft.player;
            if (localPlayer == null) return;
            PlayerMixinExtension playerExt = (PlayerMixinExtension) localPlayer;
            Skill[] allSkills = Skill.values();
            ArrayList<Skill> ownedSkills = new ArrayList<>();

            for (Skill skill : allSkills) {
                if (playerExt.wuxia$getAllSkills().getOrDefault(skill, 0) > 0) {
                    ownedSkills.add(skill);
                }
            }
            ownedSkills.sort(Comparator.comparingInt(skill -> skill.rarity.sortNum));

            for (Skill skill : ownedSkills) {
                this.addEntry(new Entry(skill));
            }
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        @Override
        protected void extractListBackground(final GuiGraphicsExtractor graphics) {
            int color = 0xDD000099;
            graphics.fill(
                    (SkillsScreen.this.width - this.getRowWidth()) / 2 - 10 - 25, this.getY() - 10,
                    (SkillsScreen.this.width - this.getRowWidth()) / 2, this.getY() + this.getHeight(),
                    color
            ); // left
            graphics.fill(
                    (SkillsScreen.this.width - this.getRowWidth()) / 2 - 10 - 25, this.getY() + this.getHeight(),
                    (SkillsScreen.this.width + this.getRowWidth()) / 2, this.getY() + this.getHeight() + 10,
                    color
            ); // down
            graphics.fill(
                    (SkillsScreen.this.width + this.getRowWidth()) / 2, this.getY(),
                    (SkillsScreen.this.width + this.getRowWidth()) / 2 + 10 + 25, this.getY() + this.getHeight() + 10,
                    color
            ); // right
            graphics.fill(
                    (SkillsScreen.this.width - this.getRowWidth()) / 2, this.getY() - 10,
                    (SkillsScreen.this.width + this.getRowWidth()) / 2 + 10 + 25, this.getY(),
                    color
            ); // up
        }

        @Override
        protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Skill skill;
            private final Component skillDisplay;
            private static final int BUTTON_WIDTH = 40;
            private static final int BUTTON_HEIGHT = 20;

            private Entry(final Skill skill) {
                super();
                this.skill = skill;
                this.skillDisplay = formatSkillName(Component.translatable(this.skill.translationKey));
            }

            @Override
            public @NonNull Component getNarration() {
                return skillDisplay;
            }

            private String getValueText() {
                LocalPlayer localPlayer = SkillsScreen.this.minecraft.player;
                if (localPlayer == null) return "?";
                PlayerMixinExtension playerExt = (PlayerMixinExtension) localPlayer;
                Map<Skill, Integer> skills = playerExt.wuxia$getAllSkills();
                Integer value = skills.get(this.skill);
                return value != null ? value.toString() : "0";
            }

            private int getButtonX() {
                String levelText = this.getValueText();
                return this.getContentRight() - SkillsScreen.this.font.width(levelText) - 8 - BUTTON_WIDTH;
            }

            private int getButtonY() {
                return this.getContentYMiddle() - BUTTON_HEIGHT / 2;
            }

            private boolean isEquipButtonHovered(int mouseX, int mouseY) {
                int buttonX = this.getButtonX();
                int buttonY = this.getButtonY();
                return mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH
                        && mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT;
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                double mouseX = event.x();
                double mouseY = event.y();
                if (this.isEquipButtonHovered((int) mouseX, (int) mouseY) && this.skill.rarity != Rarity.COMMON) {
                    if (event.button() == 0) {
                        Player player = SkillsScreen.this.minecraft.player;
                        if (player != null) {
                            for (Skill.Type type : this.skill.types) {
                                EquipSkillC2SPayload payload = new EquipSkillC2SPayload(type.name(), this.skill.name());
                                ClientPlayNetworking.send(payload);
                            }
                        }
                        SkillsList.this.refreshEntries();
                    }
                } else {
                    SkillsScreen.this.minecraft.setScreenAndShow(new SkillScreen(SkillsScreen.this, this.skill));
                }
                return true;
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int y = this.getContentYMiddle() - 9 / 2;
                graphics.text(SkillsScreen.this.font, this.skillDisplay, this.getContentX() + 2, y, this.skill.rarity.color);

                String levelText = this.getValueText();
                int levelX = this.getContentRight() - SkillsScreen.this.font.width(levelText) - 4;
                graphics.text(SkillsScreen.this.font, levelText, levelX, y, 0xFFFFFFFF);

                PlayerMixinExtension playerExt = (PlayerMixinExtension) SkillsScreen.this.minecraft.player;
                if (playerExt == null) return;
                Skill[] equippedSkills = playerExt.wuxia$getEquippedSkills();
                if (this.skill.rarity != Rarity.COMMON) {
                    int buttonX = this.getButtonX();
                    int buttonY = this.getButtonY();
                    boolean buttonHovered = this.isEquipButtonHovered(mouseX, mouseY);
                    int buttonColor = buttonHovered ? 0xFFAAAAAA : 0xFF555555;
                    graphics.fill(buttonX, buttonY, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, buttonColor);
                    graphics.fill(buttonX, buttonY, buttonX + BUTTON_WIDTH, buttonY + 1, 0xFFFFFFFF);
                    graphics.fill(buttonX, buttonY + BUTTON_HEIGHT - 1, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, 0xFFFFFFFF);
                    graphics.fill(buttonX, buttonY, buttonX + 1, buttonY + BUTTON_HEIGHT, 0xFFFFFFFF);
                    graphics.fill(buttonX + BUTTON_WIDTH - 1, buttonY, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, 0xFFFFFFFF);

                    Component text = Component.translatable("skills_screen.equip");

                    int textX = buttonX + (BUTTON_WIDTH - SkillsScreen.this.font.width(text)) / 2;
                    int textY = buttonY + (BUTTON_HEIGHT - 9) / 2;
                    graphics.text(SkillsScreen.this.font, text, textX, textY, 0xFFFFFFFF);
                } else {
                    if (equippedSkills[this.skill.types[0].ordinal()] != this.skill) {
                        Component component = Component.translatable("skills_screen.equipped")
                                .append(Component.translatable(equippedSkills[this.skill.types[0].ordinal()].translationKey));
                        graphics.text(SkillsScreen.this.font, component, this.getContentX() + 60, y, equippedSkills[this.skill.types[0].ordinal()].rarity.color);
                    }
                }
            }

            private static Component formatSkillName(Component skillName) {
                String skillNameString = skillName.getString();
                if (skillNameString.length() > 5) {
                    return Component.literal(skillNameString.substring(0, 4) + "...");
                } else {
                    return skillName;
                }
            }
        }
    }
}

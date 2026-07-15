package com.aiden.wuxia.client.screen;

import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.WuxiaAttributesC2SPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class AttributesScreen extends Screen {
    public final Screen parent;
    private String health;
    private String mana, maxMana, innateStrength, acquiredStrength, innateConstitution, acquiredConstitution, innateAgility, acquiredAgility, innateWisdom, acquiredWisdom;
    private String attack, defense, accuracy, dodge, parry;
    private String basicAttributes, combatAttributes;
    private int bgColor = 0xDD000099;

    public AttributesScreen(Screen parent) {
        super(Component.translatable("wuxiaScreen.attributes"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // send packet
        WuxiaAttributesC2SPayload payload = new WuxiaAttributesC2SPayload();
        ClientPlayNetworking.send(payload);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                _ -> this.minecraft.setScreen(this.parent)
        ).pos(this.width / 2 - 100, this.height - 35).width(200).build());
        this.refreshAttributes();
    }

    public void refreshAttributes() {
        if (this.minecraft.player == null) {
            minecraft.setScreen(null);
            return;
        }
        PlayerMixinExtension playerExt = (PlayerMixinExtension) this.minecraft.player;

        this.combatAttributes = Component.translatable("attributesScreen.combat_attributes").getString();
        this.basicAttributes = Component.translatable("attributesScreen.basic_attributes").getString();

        if (playerExt == null) {
            this.mana = this.maxMana = this.innateStrength = this.acquiredStrength = this.innateConstitution = this.acquiredConstitution = this.innateAgility = this.acquiredAgility = this.innateWisdom = this.acquiredWisdom = "";
            this.attack = this.defense = this.accuracy = "";
            return;
        }

        this.health = Component.translatable("attributesScreen.health").getString() + ": " + playerExt.wuxia$getHealth() + " / " + playerExt.wuxia$getMaxHealth();
        this.mana = Component.translatable("attributesScreen.mana").getString() + ": " + playerExt.wuxia$getMana();
        this.maxMana = Component.translatable("attributesScreen.max_mana").getString() + ": " + playerExt.wuxia$getMaxMana();
        this.innateStrength = Component.translatable("attributesScreen.innate_strength").getString() + ": " + playerExt.wuxia$getInnateStrength();
        this.acquiredStrength = Component.translatable("attributesScreen.acquired_strength").getString() + ": " + playerExt.wuxia$getAcquiredStrength();
        this.innateConstitution = Component.translatable("attributesScreen.innate_constitution").getString() + ": " + playerExt.wuxia$getInnateConstitution();
        this.acquiredConstitution = Component.translatable("attributesScreen.acquired_constitution").getString() + ": " + playerExt.wuxia$getAcquiredConstitution();
        this.innateAgility = Component.translatable("attributesScreen.innate_agility").getString() + ": " + playerExt.wuxia$getInnateAgility();
        this.acquiredAgility = Component.translatable("attributesScreen.acquired_agility").getString() + ": " + playerExt.wuxia$getAcquiredAgility();
        this.innateWisdom = Component.translatable("attributesScreen.innate_wisdom").getString() + ": " + playerExt.wuxia$getInnateWisdom();
        this.acquiredWisdom = Component.translatable("attributesScreen.acquired_wisdom").getString() + ": " + playerExt.wuxia$getAcquiredWisdom();

        this.attack = Component.translatable("attributesScreen.attack").getString() + ": " + playerExt.wuxia$getAttack();
        this.defense = Component.translatable("attributesScreen.defense").getString() + ": " + playerExt.wuxia$getDefense();
        this.accuracy = Component.translatable("attributesScreen.accuracy").getString() + ": " + playerExt.wuxia$getAccuracy();
        this.dodge = Component.translatable("attributesScreen.dodge").getString() + ": " + playerExt.wuxia$getDodge();
        this.parry = Component.translatable("attributesScreen.parry").getString() + ": " + playerExt.wuxia$getParry();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.fill(30, 35, 450, 215, this.bgColor);
        graphics.text(this.font, this.title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.basicAttributes, 40, 60 - this.font.lineHeight - 10, 0xFFFFFF55, true);
        graphics.text(this.font, this.health, 40, 80 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.mana, 40, 90 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.maxMana, 40, 100 - this.font.lineHeight - 10, 0xFFFFFFFF, true);

        graphics.text(this.font, this.combatAttributes, 160, 60 - this.font.lineHeight - 10, 0xFFFFFF55, true);
        graphics.text(this.font, this.innateStrength, 160, 80 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredStrength, 160, 90 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateConstitution, 160, 100 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredConstitution, 160, 110 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateAgility, 160, 120 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredAgility, 160, 130 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateWisdom, 160, 140 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredWisdom, 160, 150 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.attack, 280, 80 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.defense, 280, 90 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.accuracy, 280, 100 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.dodge, 280, 110 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.parry, 280, 120 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

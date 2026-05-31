package com.aiden.wuxia.screen;

import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.WSAttributesC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class AttributesScreen extends Screen {
    public Screen parent;
    private String health;
    private String mana, maxMana, innateStrength, acquiredStrength, innateConstitution, acquiredConstitution, innateAgility, acquiredAgility, innateWisdom, acquiredWisdom;
    private String attack, defense, accuracy;
    private String basicAttributes, combatAttributes;

    public AttributesScreen(Screen parent) {
        super(Component.translatable("wuxiaScreen.attributes"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.minecraft.player == null) minecraft.setScreen(null);
        PlayerMixinExtension playerExt = (PlayerMixinExtension) this.minecraft.player;

        // send packet
        WSAttributesC2SPayload payload = new WSAttributesC2SPayload();
        ClientPlayNetworking.send(payload);

        this.health = Component.translatable("attributesScreen.health").getString() + ": " + Math.round(this.minecraft.player.getHealth());
        this.combatAttributes = Component.translatable("attributesScreen.combat_attributes").getString();
        this.basicAttributes = Component.translatable("attributesScreen.basic_attributes").getString();

        if (playerExt == null) {
            this.mana = this.maxMana = this.innateStrength = this.acquiredStrength = this.innateConstitution = this.acquiredConstitution = this.innateAgility = this.acquiredAgility = this.innateWisdom = this.acquiredWisdom = "";
            this.attack = this.defense = this.accuracy = "";
            return;
        }

        this.mana = Component.translatable("attributesScreen.mana").getString() + ": " + playerExt.ws$getMana();
        this.maxMana = Component.translatable("attributesScreen.max_mana").getString() + ": " + playerExt.ws$getMaxMana();
        this.innateStrength = Component.translatable("attributesScreen.innate_strength").getString() + ": " + playerExt.ws$getInnateStrength();
        this.acquiredStrength = Component.translatable("attributesScreen.acquired_strength").getString() + ": " + playerExt.ws$getAcquiredStrength();
        this.innateConstitution = Component.translatable("attributesScreen.innate_constitution").getString() + ": " + playerExt.ws$getInnateConstitution();
        this.acquiredConstitution = Component.translatable("attributesScreen.acquired_constitution").getString() + ": " + playerExt.ws$getAcquiredConstitution();
        this.innateAgility = Component.translatable("attributesScreen.innate_agility").getString() + ": " + playerExt.ws$getInnateAgility();
        this.acquiredAgility = Component.translatable("attributesScreen.acquired_agility").getString() + ": " + playerExt.ws$getAcquiredAgility();
        this.innateWisdom = Component.translatable("attributesScreen.innate_wisdom").getString() + ": " + playerExt.ws$getInnateWisdom();
        this.acquiredWisdom = Component.translatable("attributesScreen.acquired_wisdom").getString() + ": " + playerExt.ws$getAcquiredWisdom();

        this.attack = Component.translatable("attributesScreen.attack").getString() + ": " + playerExt.ws$getAttack();
        this.defense = Component.translatable("attributesScreen.defense").getString() + ": " + playerExt.ws$getDefense();
        this.accuracy = Component.translatable("attributesScreen.accuracy").getString() + ": " + playerExt.ws$getAccuracy();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.fill(30, 35, 450, 215, 0xDD000099);
        graphics.text(this.font, this.title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.basicAttributes, 40, 60 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.health, 40, 80 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.mana, 40, 90 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.maxMana, 40, 100 - this.font.lineHeight - 10, 0xFFFFFFFF, true);

        graphics.text(this.font, this.combatAttributes, 160, 60 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateStrength, 160, 80 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredStrength, 160, 90 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateConstitution, 160, 100 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredConstitution, 160, 110 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateAgility, 160, 120 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredAgility, 160, 130 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.innateWisdom, 160, 140 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.acquiredWisdom, 160, 150 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.attack, 160, 160 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.defense, 160, 170 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
        graphics.text(this.font, this.accuracy, 160, 180 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}

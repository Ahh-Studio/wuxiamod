package com.aiden.wuxia.mixin_extension;

public interface PlayerMixinExtension {
    int[] ws$getAllAttributes();
    void ws$setAllAttributes(int[] attributes);
    int ws$getMana();
    void ws$setMana(int mana);
    int ws$getMaxMana();
    void ws$setMaxMana(int mana);
    int ws$getInnateStrength();
    void ws$setInnateStrength(int innateStrength);
    int ws$getAcquiredStrength();
    void ws$setAcquiredStrength(int acquiredStrength);
    int ws$getInnateConstitution();
    void ws$setInnateConstitution(int innateConstitution);
    int ws$getAcquiredConstitution();
    void ws$setAcquiredConstitution(int acquiredConstitution);
    int ws$getInnateAgility();
    void ws$setInnateAgility(int innateAgility);
    int ws$getAcquiredAgility();
    void ws$setAcquiredAgility(int acquiredAgility);
    int ws$getInnateWisdom();
    void ws$setInnateWisdom(int innateWisdom);
    int ws$getAcquiredWisdom();
    void ws$setAcquiredWisdom(int acquiredWisdom);
    int ws$getAttack();
    void ws$setAttack(int attack);
    int ws$getDefense();
    void ws$setDefense(int defense);
    int ws$getAccuracy();
    void ws$setAccuracy(int accuracy);
}

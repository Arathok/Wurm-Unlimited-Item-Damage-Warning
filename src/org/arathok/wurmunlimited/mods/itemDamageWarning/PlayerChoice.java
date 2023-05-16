package org.arathok.wurmunlimited.mods.itemDamageWarning;

public class PlayerChoice {
    public float previousDamageSetting;
    public boolean previousWarningType;
    public long playerId;

    public PlayerChoice(float aPreviousDamageSetting, boolean aPreviousWarningType, long aPlayerId)
    {
        this.playerId=aPlayerId;
        this.previousDamageSetting=aPreviousDamageSetting;
        this.previousWarningType=aPreviousWarningType;
    }
}

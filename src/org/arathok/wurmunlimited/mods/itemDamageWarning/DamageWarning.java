package org.arathok.wurmunlimited.mods.itemDamageWarning;

public class DamageWarning {
    public long itemId =0;
    public long playerId=0;
    public float targetDamage = 0f;
    public boolean warningType=false;
    public float previousDamageSetting;
    public boolean previousWarningType;

    public DamageWarning(long itemid, long playerid, float targetDamage,boolean warningType, float previousDamageSetting, boolean previousWarningType)
    {
        this.itemId=itemid;
        this.playerId=playerid;
        this.targetDamage=targetDamage;
        this.warningType=warningType;
        this.previousDamageSetting = previousDamageSetting;
        this.previousWarningType=previousWarningType;
    }

}

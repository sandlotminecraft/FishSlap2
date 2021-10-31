package me.stipe.fishslap.configs;

public class MainConfig {
    private final int joinTimer = 10;
    private final int engagementTimer = 30;
    private final int pointsPerKill = 100;
    private final int killingBlowBonus = 50;
    private final int pointsLostPerDeath = 50;
    private final boolean enabledEquipEffectsIfNotPlaying = false;
    private final boolean isDebug = true;
    public MainConfig() {

    }

    public int getJoinTimer() {
        return joinTimer;
    }

    public int getEngagementTimer() {
        return engagementTimer;
    }

    public int getPointsPerKill() {
        return pointsPerKill;
    }

    public int getKillingBlowBonus() {
        return killingBlowBonus;
    }

    public int getPointsLostPerDeath() {
        return pointsLostPerDeath;
    }

    public boolean isEnabledEquipEffectsIfNotPlaying() {
        return enabledEquipEffectsIfNotPlaying;
    }

    public boolean isDebug() {
        return isDebug;
    }


}

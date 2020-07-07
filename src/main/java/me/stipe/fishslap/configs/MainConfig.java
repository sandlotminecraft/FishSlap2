package me.stipe.fishslap.configs;

import lombok.Getter;

@Getter
public class MainConfig {
    private final int joinTimer = 10;
    private final int engagementTimer = 30;
    private final int pointsPerKill = 100;
    private final int killingBlowBonus = 50;
    private final int pointsLostPerDeath = 50;
    private final boolean enabledEquipEffectsIfNotPlaying = false;

    public MainConfig() {

    }

}

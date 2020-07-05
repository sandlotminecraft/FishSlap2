package me.stipe.fishslap.configs;

import me.stipe.fishslap.types.FishConfig;
import me.stipe.fishslap.types.FishMeta;

public class SalmonConfig extends FishConfig {

    private double[] defaultDamage = {1,2,2,3,4,4,5,6,6,7};
    private double[] defaultArmor = {1,2,3,4,5,6,7,8,9,10};
    private double[] defaultAttackSpeed = {2,2,2,2,2,2,2,2,2,2};
    private double[] defaultToughness = {0,0,0,0,0,0,0,0,0,0};
    private double[] defaultKnockbackResist = {0,0,0,0,0,0,0,0,0,0};
    private double[] defaultLuckBonus = {0,0,0,0,0,0,0,0,0,0};
    private double[] defaultHealthBonus = {0,0,2,2,4,4,6,6,8,8};
    private double[] defaultSpeedBonus = {0,0,0,0,0,0,0,0,0,0};

    private String[][] defaultEnchants = new String[][]{
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
    };
    private String[][] defaultEquipEffects = new String[][]{
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
    };
    private String[][] defaultUseEffects = new String[][]{
            new String[]{"increase_damage 0"},
            new String[]{"increase_damage 0"},
            new String[]{"increase_damage 0"},
            new String[]{"increase_damage 1"},
            new String[]{"increase_damage 1"},
            new String[]{"increase_damage 1"},
            new String[]{"increase_damage 2"},
            new String[]{"increase_damage 2"},
            new String[]{"increase_damage 2"},
            new String[]{"increase_damage 3"},
    };
    private String[][] defaultAbilities = new String[][]{
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{},
    };
    private int[] defaultUseEffectDuration = {5,6,7,8,9,10,10,12,12,12};
    private int[] defaultUseEffectCooldown = {45,45,40,40,35,35,30,30,25,25};
    private int[] defaultXp = {100,150,200,300,400,500,600,800,1000,2000};

    public SalmonConfig() {
        super("salmon");

        displayName = "&dA Slimy Salmon";
        for (int i = 0; i < defaultDamage.length; i++) {
            fishStats.put(i + 1, new FishMeta(defaultDamage[i], defaultArmor[i], defaultAttackSpeed[i], defaultToughness[i],
                    defaultKnockbackResist[i]/100, defaultLuckBonus[i], defaultHealthBonus[i], defaultSpeedBonus[i]/100, defaultEnchants[i],
                    defaultEquipEffects[i], defaultUseEffects[i], defaultAbilities[i], defaultUseEffectDuration[i], defaultUseEffectCooldown[i], defaultXp[i]));
        }
    }
}

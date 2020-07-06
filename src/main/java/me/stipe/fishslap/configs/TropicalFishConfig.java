package me.stipe.fishslap.configs;

import me.stipe.fishslap.types.FishConfig;
import me.stipe.fishslap.types.FishMeta;

public class TropicalFishConfig extends FishConfig {

    private final double[] defaultDamage = {1,1,2,2,3,3,4,4,5,5};
    private final double[] defaultArmor = {0,0,2,2,2,4,4,4,6,6};
    private final double[] defaultAttackSpeed = {0.5,0.5,0.5,0.5,0.5,0.75,0.75,0.75,1,1};
    private final double[] defaultToughness = {0,0,0,0,0,0,0,0,0,0};
    private final double[] defaultKnockbackResist = {0,5,10,15,20,25,30,35,40,45};
    private final double[] defaultLuckBonus = {0,0,0,0,0,0,0,0,0,0};
    private final double[] defaultHealthBonus = {2,4,6,8,10,12,14,16,18,20};
    private final double[] defaultSpeedBonus = {0,0,0,0,0,0,0,0,0,0};

    private String[][] defaultEnchants = new String[][]{
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
            new String[]{"healing_touch 1"},
    };
    private String[][] defaultEquipEffects = new String[][]{
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{"regeneration 0"},
            new String[]{"regeneration 0"},
            new String[]{"regeneration 0"},
            new String[]{"regeneration 1"},
            new String[]{"regeneration 1"},
            new String[]{"regeneration 1"},
            new String[]{"regeneration 1"},
    };
    private String[][] defaultUseEffects = new String[][]{
            new String[]{"regeneration 0"},
            new String[]{"regeneration 0"},
            new String[]{"regeneration 0"},
            new String[]{"absorption 0"},
            new String[]{"absorption 0"},
            new String[]{"absorption 1"},
            new String[]{"absorption 1"},
            new String[]{"absorption 2"},
            new String[]{"absorption 2"},
            new String[]{"absorption 3"},
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
    private int[] defaultUseEffectDuration = {12,16,20,30,40,40,50,50,60,60};
    private int[] defaultUseEffectCooldown = {30,30,30,60,60,60,45,45,30,30};
    private int[] defaultXp = {100,150,200,300,400,500,600,800,1000,2000};

    public TropicalFishConfig() {
        super("tropical");

        displayName = "&6A Grody Grouper";
        for (int i = 0; i < defaultDamage.length; i++) {
            fishStats.put(i + 1, new FishMeta(defaultDamage[i], defaultArmor[i], defaultAttackSpeed[i], defaultToughness[i],
                    defaultKnockbackResist[i]/100, defaultLuckBonus[i], defaultHealthBonus[i], defaultSpeedBonus[i]/100, defaultEnchants[i],
                    defaultEquipEffects[i], defaultUseEffects[i], defaultAbilities[i], defaultUseEffectDuration[i], defaultUseEffectCooldown[i], defaultXp[i]));
        }
    }
}

package me.stipe.fishslap.configs;

import me.stipe.fishslap.types.FishConfig;
import me.stipe.fishslap.types.LevelData;

public class CodConfig extends FishConfig {

    private double[] defaultDamage = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5};
    private double[] defaultArmor = {1,2,3,4,5,6,7,8,9,10};
    private double[] defaultSpeed = {1,1,1,1,1,1,1,1,1,1};
    private String[][] defaultEnchants = new String[][]{
            new String[]{"knockback 1"},
            new String[]{"knockback 1"},
            new String[]{"knockback 1"},
            new String[]{"knockback 1"},
            new String[]{"knockback 2"},
            new String[]{"knockback 2"},
            new String[]{"knockback 2"},
            new String[]{"knockback 2"},
            new String[]{"knockback 2"},
            new String[]{"knockback 3"},
    };
    private String[][] defaultEquipEffects = new String[][]{
            new String[]{"damage_resistance 0"},
            new String[]{"damage_resistance 0"},
            new String[]{"damage_resistance 1"},
            new String[]{"damage_resistance 1"},
            new String[]{"damage_resistance 1"},
            new String[]{"damage_resistance 2"},
            new String[]{"damage_resistance 2"},
            new String[]{"damage_resistance 2"},
            new String[]{"damage_resistance 3"},
            new String[]{"damage_resistance 3"},
    };
    private String[][] defaultUseEffects = new String[][]{
            new String[]{},
            new String[]{},
            new String[]{},
            new String[]{"absorption 0"},
            new String[]{"absorption 0"},
            new String[]{"absorption 0"},
            new String[]{"absorption 1"},
            new String[]{"absorption 1"},
            new String[]{"absorption 1"},
            new String[]{"absorption 2"},
    };
    private int[] defaultUseEffectDuration = {0,0,0,8,10,12,12,12,12,12};
    private int[] defaultUseEffectCooldown = {0,0,0,60,60,60,60,55,50,45};
    private int[] defaultXp = {100,150,200,300,400,500,600,800,1000,2000};

    private String testEntry = "this is a test";

    public CodConfig() {
        super("cod");

        displayName = "&9&oA Crusty Cod";
        for (int i = 0; i < defaultDamage.length; i++) {
            fishStats.put(i + 1, new LevelData(defaultDamage[i], defaultArmor[i], defaultSpeed[i], defaultEnchants[i],
                    defaultEquipEffects[i], defaultUseEffects[i], defaultUseEffectDuration[i], defaultUseEffectCooldown[i], defaultXp[i]));
        }
    }

    @Override
    public void load() {
        super.load();
        testEntry = config.getString("Test Entry");
    }

    @Override
    public void save() {
        config.set("Test Entry", testEntry);
        super.save();
    }

}

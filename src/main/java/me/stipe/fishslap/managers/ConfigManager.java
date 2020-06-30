package me.stipe.fishslap.managers;

import me.stipe.fishslap.configs.*;
import me.stipe.fishslap.types.FishConfig;

public class ConfigManager {
    private final CodConfig codConfig;
    private final SalmonConfig salmonConfig;
    private final TropicalFishConfig tropicalFishConfig;
    private final PufferfishConfig pufferfishConfig;
    private final MainConfig mainConfig;
    private final Translations translations;

    public ConfigManager() {
        codConfig = new CodConfig();
        salmonConfig = new SalmonConfig();
        tropicalFishConfig = new TropicalFishConfig();
        pufferfishConfig = new PufferfishConfig();
        mainConfig = new MainConfig();
        translations = new Translations();
    }

    public void loadConfigs() {
        for (FishConfig config : new FishConfig[] { codConfig, salmonConfig, tropicalFishConfig, pufferfishConfig }) {
            config.load();
        }
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public CodConfig getCodConfig() {
        return codConfig;
    }

    public SalmonConfig getSalmonConfig() {
        return salmonConfig;
    }

    public TropicalFishConfig getTropicalFishConfig() {
        return tropicalFishConfig;
    }

    public Translations getTranslations() {
        return translations;
    }

    public PufferfishConfig getPufferfishConfig() {
        return pufferfishConfig;
    }
}

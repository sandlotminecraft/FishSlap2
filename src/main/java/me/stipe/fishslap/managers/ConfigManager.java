package me.stipe.fishslap.managers;

import me.stipe.fishslap.configs.MainConfig;
import me.stipe.fishslap.configs.Translations;
import me.stipe.fishslap.types.FishConfig;
import me.stipe.fishslap.configs.CodConfig;
import me.stipe.fishslap.configs.SalmonConfig;

public class ConfigManager {
    private final CodConfig codConfig;
    private final SalmonConfig salmonConfig;
    private final MainConfig mainConfig;
    private final Translations translations;

    public ConfigManager() {
        codConfig = new CodConfig();
        salmonConfig = new SalmonConfig();
        mainConfig = new MainConfig();
        translations = new Translations();
    }

    public void loadConfigs() {
        for (FishConfig config : new FishConfig[] { codConfig, salmonConfig }) {
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

    public Translations getTranslations() {
        return translations;
    }
}

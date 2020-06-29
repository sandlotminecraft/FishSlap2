package me.stipe.fishslap.managers;

import me.stipe.fishslap.types.FishConfig;
import me.stipe.fishslap.configs.CodConfig;
import me.stipe.fishslap.configs.SalmonConfig;

public class ConfigManager {
    private final CodConfig codConfig = new CodConfig();
    private final SalmonConfig salmonConfig = new SalmonConfig();

    public void loadConfigs() {
        for (FishConfig config : new FishConfig[] { codConfig, salmonConfig }) {
            config.load();
        }
    }

    public CodConfig getCodConfig() {
        return codConfig;
    }

    public SalmonConfig getSalmonConfig() {
        return salmonConfig;
    }
}

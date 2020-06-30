package me.stipe.fishslap.configs;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Translations {
    private final String spectatorBossBarTitle = "&eJoining FishSlap in %s seconds";
    private final String spectatorBoardTitle = "&6&lCome Play FishSlap!";
    private final String topScoresTitle = "&e&lTop Scores&r";
    private final String spectatorBoardPlayers = "&e\u25B6 &bCurrent Players: &e%s";
    private final String spectatorBoardTopScore = "&e\u25B6 &bCurrent Top Score: &e%s";
    private final String playerJoinedGameMessage = "&e[&6FishSlap&e] %s has joined the game.";
    private final String playerJoiningTitleMessage = "&bJoining FishSlap in %s Seconds...";
    private final String playerJoiningSubtitleMessage = "&eRemove Your Offhand Fish to Cancel";
    private final String playerJoinCancelledTitle = "&cJoin Cancelled!";
    private final List<String> spectatorBoardInfoText = new ArrayList<>();

    public Translations() {
        loadDefaults();
    }

    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private void loadDefaults() {
        spectatorBoardInfoText.add("  ");
        spectatorBoardInfoText.add(ChatColor.LIGHT_PURPLE + "     Catch a fish and hold it in     ");
        spectatorBoardInfoText.add(ChatColor.LIGHT_PURPLE + "       your off hand to play!       ");
        spectatorBoardInfoText.add("   ");
        spectatorBoardInfoText.add(colorize("     &e&lwww.sandlotminecraft.com"));
    }

}

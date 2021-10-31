package me.stipe.fishslap.configs;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Translations {
    private final String giveFishFailed = "&e[&6FishSlap&e] that didn't work. (%s)";
    private final String playerInventoryFull = "&e[&6FishSlap&e] %s's inventory is full.";
    private final String spectatorBossBarTitle = "&eJoining FishSlap in %s seconds";
    private final String spectatorBoardTitle = "&6&lCome Play FishSlap!";
    private final String topScoresTitle = "&e&lFishSlap Top Scores&r";
    private final String spectatorBoardPlayers = "&e\u25B6 &bCurrent Players: &e%s";
    private final String spectatorBoardTopScore = "&e\u25B6 &bCurrent Top Score: &e%s";
    private final String messagePlayerJoinedGame = "&e[&6FishSlap&e] %s has joined the game.";
    private final String titlePlayerJoiningTitle = "&bJoining FishSlap in %s Seconds...";
    private final String titlePlayerJoiningSubtitle = "&eRemove Your Offhand Fish to Cancel";
    private final String titlePlayerJoinCancelled = "&cJoin Cancelled!";
    private final String messagePlayerNotPlaying = "&e[&6FishSlap&e] You must join the game before doing that";
    private final String messageTargetNotPlaying = "&e[&6FishSlap&e] %s is not currently playing";
    private final String actionBarPlayerKilledPointsAward = "&d%s died. You earn &b%s &dpoints (%.0f%% of damage done)";
    private final String actionBarPlayerKillingBlow = "&dYou killed %s. You earn &b%s &dpoints (%.0f%% damage, %s kb bonus)";
    private final String actionBarPlayerKilledNoPoints = "&d%s died. You earned no points due to diminishing returns";
    private final String actionBarPlayerDied = "&dYou died. You lost &b%s &dpoints";
    private final String actionBarPowerupCooldown = "&e[&6Powerup&e]: %s - %d:%02d remaining";
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

    public String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1).toLowerCase();
        }
        return StringUtils.join(names, " ");
    }

    public String integerToRomanNumeral(int input) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV"};

        if (input > 15)
            return "";

        return numerals[input];
    }

    public String getActionBarPowerupCooldown() {
        return actionBarPowerupCooldown;
    }

    public String getTopScoresTitle() {
        return topScoresTitle;
    }

    public String getSpectatorBoardTitle() {
        return spectatorBoardTitle;
    }

    public List<String> getSpectatorBoardInfoText() {
        return spectatorBoardInfoText;
    }

    public String getSpectatorBoardPlayers() {
        return spectatorBoardPlayers;
    }

    public String getSpectatorBoardTopScore() {
        return spectatorBoardTopScore;
    }

    public String getMessagePlayerJoinedGame() {
        return messagePlayerJoinedGame;
    }

    public String getSpectatorBossBarTitle() {
        return spectatorBossBarTitle;
    }

    public String getTitlePlayerJoiningSubtitle() {
        return titlePlayerJoiningSubtitle;
    }

    public String getMessagePlayerNotPlaying() {
        return messagePlayerNotPlaying;
    }

    public String getTitlePlayerJoinCancelled() {
        return titlePlayerJoinCancelled;
    }

    public String getMessageTargetNotPlaying() {
        return messageTargetNotPlaying;
    }

    public String getTitlePlayerJoiningTitle() {
        return titlePlayerJoiningTitle;
    }

    public String getActionBarPlayerDied() {
        return actionBarPlayerDied;
    }

    public String getActionBarPlayerKilledNoPoints() {
        return actionBarPlayerKilledNoPoints;
    }

    public String getActionBarPlayerKilledPointsAward() {
        return actionBarPlayerKilledPointsAward;
    }

    public String getActionBarPlayerKillingBlow() {
        return actionBarPlayerKillingBlow;
    }

    public String getPlayerInventoryFull() {
        return playerInventoryFull;
    }

    public String getGiveFishFailed() {
        return giveFishFailed;
    }
}

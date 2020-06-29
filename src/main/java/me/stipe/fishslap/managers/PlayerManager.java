package me.stipe.fishslap.managers;

import me.stipe.fishslap.events.FishSlapEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private ScoreboardManager manager;
    private Scoreboard scoreboard;
    private Scoreboard spectatorBoard;
    private Objective spectatorInfo;
    private Objective topScores;
    private Objective health;

    private List<UUID> playerList = new ArrayList<>();

    public PlayerManager() {
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        topScores = scoreboard.registerNewObjective("scores", "dummy", ChatColor.translateAlternateColorCodes('&',"&e&lTop Scores&r"));
        health = scoreboard.registerNewObjective("Health", "health", ChatColor.RED + "\u2665");

        topScores.setDisplaySlot(DisplaySlot.SIDEBAR);
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);

        spectatorBoard = manager.getNewScoreboard();
        spectatorInfo = spectatorBoard.registerNewObjective("info", "dummy", ChatColor.GOLD + "Come Play FishSlap!");
        spectatorInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
        spectatorInfo.getScore("").setScore(5);
        spectatorInfo.getScore("There is currently nobody playing").setScore(4);
        spectatorInfo.getScore("Current Top Score: 0").setScore(3);
        spectatorInfo.getScore(" ").setScore(2);
        spectatorInfo.getScore(ChatColor.LIGHT_PURPLE + " Catch a fish and hold it in").setScore(1);
        spectatorInfo.getScore(ChatColor.LIGHT_PURPLE + " your off hand to play!").setScore(0);
    }

    // manage scores
    public void addToScore(Player p, String target, int rawAmount) {
        Score score = topScores.getScore(ChatColor.stripColor(p.getDisplayName()));
    }

    public void sendSpectatorScoreboard(Player p) {
        p.setScoreboard(spectatorBoard);
    }

    // add and remove players
    public void addPlayer(Player p) {

    }

    public void removePlayer(Player p) {

    }

    public void updateScores(FishSlapEvent event) {

    }

    public boolean isPlaying(Player p) {
        return false;
    }

    public boolean isJoining(Player p) {
        return false;
    }

}

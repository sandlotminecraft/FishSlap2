package me.stipe.fishslap.managers;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.events.GameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.*;

import java.util.*;

public class PlayerManager implements Listener {

    private final ScoreboardManager manager;
    private final Scoreboard scoreboard;
    private final Scoreboard spectatorBoard;
    private final Objective spectatorInfo;
    private final Objective topScores;
    private final Objective health;

    private final List<UUID> playerList = new ArrayList<>();
    private Map<BossBar, Integer> bossBars = new HashMap<>();

    // TODO - put these in config
    private final String spectatorBossBarTitle = ChatColor.YELLOW + "Joining FishSlap in %s seconds";
    private final int joinTimer = 10;

    public PlayerManager() {
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        topScores = scoreboard.registerNewObjective("scores", "dummy", ChatColor.translateAlternateColorCodes('&',"&e&lTop Scores&r"));
        health = scoreboard.registerNewObjective("Health", "health", ChatColor.RED + "\u2665");

        topScores.setDisplaySlot(DisplaySlot.SIDEBAR);
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);

        spectatorBoard = manager.getNewScoreboard();
        spectatorInfo = spectatorBoard.registerNewObjective("info", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "Come Play FishSlap!");
        spectatorInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
        spectatorInfo.getScore("").setScore(5);
        spectatorInfo.getScore("There is currently nobody playing").setScore(4);
        spectatorInfo.getScore("Current Top Score: 0").setScore(3);
        spectatorInfo.getScore(" ").setScore(2);
        spectatorInfo.getScore(ChatColor.LIGHT_PURPLE + " Catch a fish and hold it in").setScore(1);
        spectatorInfo.getScore(ChatColor.LIGHT_PURPLE + " your off hand to play!").setScore(0);
    }

    // manage scores
    public void addScore(Player p, int rawAmount) {
        Score score = topScores.getScore(ChatColor.stripColor(p.getDisplayName()));
        score.setScore(score.getScore() + rawAmount);
    }

    public void sendSpectatorScoreboard(Player p) {
        p.setScoreboard(spectatorBoard);
    }

    // add and remove players
    public void addPlayer(Player p) {
        playerList.add(p.getUniqueId());
        p.setScoreboard(scoreboard);
        addScore(p, 0);
        p.sendMessage("Joined");
    }

    public void removePlayer(Player p) {
        playerList.remove(p.getUniqueId());
        p.setScoreboard(spectatorBoard);
        p.sendMessage("Left");
    }

    public void updateScores(FishSlapEvent event) {

    }

    public boolean isPlaying(Player p) {
        return playerList.contains(p.getUniqueId());
    }

    public boolean isJoining(Player p) {
        return p.hasMetadata("joining");
    }

    private void startJoin(Player p) {
        NamespacedKey key = new NamespacedKey(FSApi.getPlugin(), p.getUniqueId().toString());
        String title = String.format(spectatorBossBarTitle, joinTimer);

        p.setMetadata("joining", new FixedMetadataValue(FSApi.getPlugin(), System.currentTimeMillis() + (1000 * joinTimer)));
        p.sendMessage("Joining");

        KeyedBossBar joinBar = Bukkit.getServer().createBossBar(key, title, BarColor.RED, BarStyle.SOLID);
        joinBar.setProgress(1.0);
        joinBar.addPlayer(p);
        bossBars.put(joinBar, joinTimer);
    }

    private void cancelJoin(Player p) {
        p.removeMetadata("joining", FSApi.getPlugin());
        p.sendMessage("Join cancelled");

        for (BossBar bar : bossBars.keySet()) {
            for (Player player : bar.getPlayers()) {
                if (player.getUniqueId().equals(p.getUniqueId())) {
                    bar.removePlayer(player);
                }
            }
        }
    }

    public void setEngaged(Player p) {
        // TODO - put this in config
        int engagementTimer = 30;

        p.setMetadata("engaged", new FixedMetadataValue(FSApi.getPlugin(), System.currentTimeMillis() + (1000 * engagementTimer)));
    }

    public boolean isEngaged(Player p) {
        return p.hasMetadata("engaged");
    }

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        Map <BossBar, Integer> newMap = new HashMap<>();
        for (BossBar bar : bossBars.keySet()) {
            int left = bossBars.get(bar) - 1;

            bar.setTitle(String.format(spectatorBossBarTitle, left));
            bar.setProgress((float) left / joinTimer);

            if (left > 0)
                newMap.put(bar, left);
            for (Player p : bar.getPlayers()) {
                if (left == 0) {
                    addPlayer(p);
                    bar.removePlayer(p);
                    p.removeMetadata("joining", FSApi.getPlugin());
                }
                if (!p.isOnline()) {
                    newMap.remove(bar);
                    bar.removePlayer(p);
                }
            }
        }
        bossBars = newMap;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isEngaged(p)) {
                int expiry = p.getMetadata("engaged").get(0).asInt();

                if (System.currentTimeMillis() > expiry)
                    p.removeMetadata("engaged", FSApi.getPlugin());
            }

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        p.removeMetadata("engaged", FSApi.getPlugin());
        p.removeMetadata("joining", FSApi.getPlugin());
        removePlayer(p);
        scoreboard.resetScores(p.getDisplayName());
    }

    @EventHandler
    public void onChangeOffhand(ChangeOffhandFishEvent event) {
        Player p = event.getPlayer();
        if (!isPlaying(p)) {
            // not playing and swapped a fish into off hand (wasn't one there already)
            if (event.getNewFish() != null && event.getOldFish() == null) {
                startJoin(p);
            }

            // not playing and removed a fish from offhand
            if (event.getNewFish() == null) {
                if (isJoining(p))
                    cancelJoin(p);
            }
        }
        else {
            if (event.getNewFish() == null) {
                // check if on cooldown and cancel

                // remove from game
                removePlayer(p);
            }
        }
    }

}

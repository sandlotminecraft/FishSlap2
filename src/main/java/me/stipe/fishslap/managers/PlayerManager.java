package me.stipe.fishslap.managers;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.configs.MainConfig;
import me.stipe.fishslap.configs.Translations;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.types.Fish;
import me.stipe.fishslap.types.FishAbility;
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

    private final Scoreboard scoreboard;
    private final Scoreboard spectatorBoard;
    private final Objective spectatorInfo;
    private final Objective topScores;
    private final MainConfig config = FSApi.getConfigManager().getMainConfig();
    private final Translations translations = FSApi.getConfigManager().getTranslations();

    private final List<UUID> playerList = new ArrayList<>();
    private final Set<FishAbility> abilities = new HashSet<>();
    private Map<BossBar, Integer> bossBars = new HashMap<>();


    public PlayerManager() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        topScores = scoreboard.registerNewObjective("scores", "dummy", translations.colorize(translations.getTopScoresTitle()));
        Objective health = scoreboard.registerNewObjective("Health", "health", ChatColor.RED + "\u2665");

        topScores.setDisplaySlot(DisplaySlot.SIDEBAR);
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);

        spectatorBoard = manager.getNewScoreboard();
        spectatorInfo = spectatorBoard.registerNewObjective("info", "dummy", translations.colorize(translations.getSpectatorBoardTitle()));
        initializeSpectatorInfo();
    }

    // manage scores
    public void addScore(Player p, int rawAmount) {
        Score score = topScores.getScore(ChatColor.stripColor(p.getDisplayName()));
        score.setScore(score.getScore() + rawAmount);
    }

    private int getTopScore() {
        int topScore = 0;
        for (String s : scoreboard.getEntries()) {
            if (topScores.getScore(s).getScore() > topScore)
                topScore = topScores.getScore(s).getScore();
        }
        return topScore;
    }

    private void initializeSpectatorInfo() {
        int line = 9;
        spectatorInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
        spectatorInfo.getScore("").setScore(line);
        line--;
        spectatorInfo.getScore(translations.colorize(String.format(translations.getSpectatorBoardPlayers(), playerList.size()))).setScore(line);
        line--;
        spectatorInfo.getScore(translations.colorize(String.format(translations.getSpectatorBoardTopScore(), getTopScore()))).setScore(line);
        line--;
        for (String s : translations.getSpectatorBoardInfoText()) {
            spectatorInfo.getScore(translations.colorize(s)).setScore(line);
            line--;
        }
    }

    public Set<FishAbility> getAbilities() {
        return abilities;
    }

    public void registerAbility(FishAbility ability) {
        abilities.add(ability);
    }

    public void unregisterAbility(FishAbility ability) {
        abilities.remove(ability);
    }

    public boolean isRegistered(FishAbility ability) {
        for (FishAbility fishAbility : abilities) {
            if (fishAbility.getName().equals(ability.getName()))
                return true;
        }
        return false;
    }

    public FishAbility getAbility(String name) {
        for (FishAbility ability : abilities) {
            if (ability.getName().equals(name))
                return ability;
        }
        return null;
    }

    private void updateSpectatorInfo() {
        for (String s : spectatorBoard.getEntries()) {
            if (s.contains("Players")) {
                int line = spectatorInfo.getScore(s).getScore();
                spectatorBoard.resetScores(s);
                spectatorInfo.getScore(translations.colorize(String.format(translations.getSpectatorBoardPlayers(), playerList.size()))).setScore(line);
            }
            if (s.contains("Top Score")) {
                int line = spectatorInfo.getScore(s).getScore();
                spectatorBoard.resetScores(s);
                spectatorInfo.getScore(translations.colorize(String.format(translations.getSpectatorBoardTopScore(), getTopScore()))).setScore(line);
            }

        }
    }

    public void sendSpectatorScoreboard(Player p) {
        p.setScoreboard(spectatorBoard);
    }

    // add and remove players
    public void addPlayer(Player p) {
        playerList.add(p.getUniqueId());
        p.setScoreboard(scoreboard);
        addScore(p, 0);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlaying(player))
                player.sendMessage(translations.colorize(String.format(translations.getPlayerJoinedGameMessage(), p.getName())));
        }

        if (!config.isEnabledEquipEffectsIfNotPlaying()) {
            Fish fish = Fish.getFromItemStack(p.getInventory().getItemInOffHand(), p);
            if (fish != null)
                fish.addEquipEffects();
        }


    }

    public void removePlayer(Player p) {
        playerList.remove(p.getUniqueId());
        p.setScoreboard(spectatorBoard);
        p.sendMessage("Left");
    }

    public boolean isPlaying(Player p) {
        return playerList.contains(p.getUniqueId());
    }

    public boolean isJoining(Player p) {
        return p.hasMetadata("joining");
    }

    private void startJoin(Player p) {
        NamespacedKey key = new NamespacedKey(FSApi.getPlugin(), p.getUniqueId().toString());
        String title = String.format(translations.colorize(translations.getSpectatorBossBarTitle()), config.getJoinTimer());

        p.setMetadata("joining", new FixedMetadataValue(FSApi.getPlugin(), System.currentTimeMillis() + (1000 * config.getJoinTimer())));

        KeyedBossBar joinBar = Bukkit.getServer().createBossBar(key, title, BarColor.RED, BarStyle.SOLID);
        joinBar.setProgress(1.0);
        joinBar.addPlayer(p);
        bossBars.put(joinBar, config.getJoinTimer());

        p.sendTitle(translations.colorize(String.format(translations.getPlayerJoiningTitleMessage(), config.getJoinTimer())),
                translations.colorize(translations.getPlayerJoiningSubtitleMessage()), 5, 70, 5);
    }

    private void cancelJoin(Player p) {
        p.removeMetadata("joining", FSApi.getPlugin());
        p.sendTitle(translations.colorize(translations.getPlayerJoinCancelledTitle()), " ", 5, 30, 5);

        for (BossBar bar : bossBars.keySet()) {
            for (Player player : bar.getPlayers()) {
                if (player.getUniqueId().equals(p.getUniqueId())) {
                    bar.removePlayer(player);
                }
            }
        }
    }

    public void setEngaged(Player p) {
        p.setMetadata("engaged", new FixedMetadataValue(FSApi.getPlugin(), System.currentTimeMillis() + (1000 * config.getEngagementTimer())));
    }

    public boolean isEngaged(Player p) {
        return p.hasMetadata("engaged");
    }

    private void tickBossBars() {
        Map <BossBar, Integer> newMap = new HashMap<>();
        for (BossBar bar : bossBars.keySet()) {
            int left = bossBars.get(bar) - 1;

            bar.setTitle(String.format(translations.colorize(translations.getSpectatorBossBarTitle()), left));
            bar.setProgress((float) left / config.getJoinTimer());

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

    }

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        updateSpectatorInfo();
        tickBossBars();

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

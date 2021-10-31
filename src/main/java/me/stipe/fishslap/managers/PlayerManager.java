package me.stipe.fishslap.managers;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.configs.MainConfig;
import me.stipe.fishslap.configs.Translations;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.types.Fish;
import me.stipe.fishslap.types.FishAbility;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
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
    private Map<String, PlayerData> playerData =  new HashMap<>();

    private class PlayerData {
        private String name;
        private List<String> killers;
        private Map<String, Double> recentDamagers;
        private int deaths;
        private double damageDone;
        private int healingDone;
        private boolean dead;

        public PlayerData(Player player) {
            this(player.getName());
        }

        public PlayerData(String name) {
            this.name = name;
            killers = new ArrayList<>();
            recentDamagers = new HashMap<>();
            deaths = 0;
            damageDone = 0;
            healingDone = 0;
            dead = false;
        }

        public void addKill() {
        }

        public void addDamageDone(double damage) {
            if (damage >= 0)
                damageDone += damage;
            else
                healingDone -= damage;
        }

        public void addDamager(String name, double damage) {
            if (recentDamagers.containsKey(name))
                recentDamagers.replace(name, recentDamagers.get(name) + damage);
            else
                recentDamagers.put(name, damage);
        }

        public void addDeath(String killer) {
            deaths++;
            recentDamagers = new HashMap<>();

            if (killer != null)
                killers.add(killer);
        }

        public boolean isDead() {
            return dead;
        }

        public void setDead(boolean dead) {
            this.dead = dead;
        }

        public List<String> getKillers() {
            return killers;
        }

        public Map<String, Double> getRecentDamagers() {
            return recentDamagers;
        }
    }

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

    public boolean isDead(Player player) {
        if (player.isDead())
            return true;

        if (playerData.containsKey(player.getName()))
            return playerData.get(player.getName()).isDead();

        return false;
    }

    public void setDead(Player player, boolean dead) {
        if (!playerData.containsKey(player.getName()))
            playerData.put(player.getName(), new PlayerData(player));

        playerData.get(player.getName()).setDead(dead);
    }

    // manage scores
    public void addScore(Player p, int rawAmount) {
        Score score = topScores.getScore(ChatColor.stripColor(p.getDisplayName()));
        score.setScore(Math.max(0, score.getScore() + rawAmount));
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
                player.sendMessage(translations.colorize(String.format(translations.getMessagePlayerJoinedGame(), p.getName())));
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

        p.sendTitle(translations.colorize(String.format(translations.getTitlePlayerJoiningTitle(), config.getJoinTimer())),
                translations.colorize(translations.getTitlePlayerJoiningSubtitle()), 5, 70, 5);
    }

    private void cancelJoin(Player p) {
        p.removeMetadata("joining", FSApi.getPlugin());
        p.sendTitle(translations.colorize(translations.getTitlePlayerJoinCancelled()), " ", 5, 30, 5);

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
                if (left <= 3 && left > 0) {
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&eJoining in:&l ") + left, "", 5, 10, 5);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1F, 1F);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1F, 3F);
                        }
                    }.runTaskLater(FSApi.getPlugin(), 5);
                }
                if (left == 0) {
                    addPlayer(p);
                    bar.removePlayer(p);
                    p.removeMetadata("joining", FSApi.getPlugin());
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lGame On!"), "", 5, 10, 5);
                    p.playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1F, 1.5F);
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

    @EventHandler (priority = EventPriority.LOW)
    public void onFishSlap(FishSlapEvent event) {
        if (event.isCancelled() || event.getDamageEvent().isCancelled())
            return;

        Player target = event.getTarget();
        Player slapper = event.getSlapper();
        double damage = Math.min(event.getDamageEvent().getFinalDamage(), target.getHealth());
        Translations translations = FSApi.getConfigManager().getTranslations();
        MainConfig config = FSApi.getConfigManager().getMainConfig();

        slapper.setGameMode(GameMode.SURVIVAL);

        if (isDead(target) || !isPlaying(target) || !isPlaying(slapper))
            return;

        if (!playerData.containsKey(slapper.getName()))
            playerData.put(slapper.getName(), new PlayerData(slapper));
        if (!playerData.containsKey(target.getName()))
            playerData.put(target.getName(), new PlayerData(target));

        playerData.get(target.getName()).addDamager(slapper.getName(), damage);
        playerData.get(slapper.getName()).addDamageDone(damage);

        // this is a killing blow so log those stats and grant points
        if (target.getHealth() == damage) {
            int pointsPerKill = config.getPointsPerKill();
            playerData.get(slapper.getName()).addKill();
            addScore(target, -config.getPointsLostPerDeath());
            target.sendActionBar('&', String.format(translations.getActionBarPlayerDied(), config.getPointsLostPerDeath()));

            double totalDamage = 0;
            for (Double d : playerData.get(target.getName()).getRecentDamagers().values()) {
                totalDamage += d;
            }

            int totalKillerCount = playerData.get(target.getName()).getKillers().size();

            for (String s : playerData.get(target.getName()).getRecentDamagers().keySet()) {
                Player p = Bukkit.getPlayer(s);
                int damageDone = (int) Math.round(playerData.get(target.getName()).getRecentDamagers().get(s));
                int killPoints = (int) Math.round(pointsPerKill * (playerData.get(target.getName()).getRecentDamagers().get(s) / totalDamage));
                int killsOnThisPlayer = Collections.frequency(playerData.get(target.getName()).getKillers(), s);
                int kbBonus = config.getKillingBlowBonus();
                float multiplier = 1;
                if (p == null) continue;

                if (killsOnThisPlayer > 3) {
                    multiplier = 1 - Math.max(0, (float) (killsOnThisPlayer - 3) / totalKillerCount);
                    if (topScores.getScore(target.getName()).getScore() <= 0) {
                        p.sendActionBar('&', String.format(translations.getActionBarPlayerKilledNoPoints(), target.getName()));
                        continue;
                    }
                }

                kbBonus *= multiplier;
                killPoints *= multiplier;
                if (!s.equals(slapper.getName())) {
                    p.sendActionBar('&', String.format(translations.getActionBarPlayerKilledPointsAward(), target.getName(), killPoints, damageDone / totalDamage * 100));
                    addScore(p, killPoints);
                }
                else {
                    slapper.sendActionBar('&', String.format(translations.getActionBarPlayerKillingBlow(), target.getName(), kbBonus + killPoints, damageDone / totalDamage * 100, kbBonus));
                    addScore(slapper, kbBonus + killPoints);
                }
            }
            playerData.get(target.getName()).addDeath(slapper.getName());

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

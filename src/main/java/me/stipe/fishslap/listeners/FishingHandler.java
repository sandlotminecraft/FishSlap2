package me.stipe.fishslap.listeners;

import lombok.Getter;
import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.managers.PlayerManager;
import me.stipe.fishslap.types.Fish;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FishingHandler implements Listener {

    List<FishingLoot> loot = new ArrayList<>();
    private String[] lootTable = new String[] {
            "50 diamond true",
            "50 iron_ingot false",
            "10 wooden_sword false",
            "10 iron_sword true",
            "25 potion false regen amplified",
            "40 stone_sword false sweeping_edge 4"
    };

    public FishingHandler() {
        populateLootFromConfig();
    }

    @Getter
    private class FishingLoot {
        ItemStack item;
        int chance;
        boolean playersOnly;

        public FishingLoot(ItemStack item, int chance, boolean playersOnly) {
            this.item = item;
            this.chance = chance;
            this.playersOnly = playersOnly;
        }
    }


    private ItemStack getFishingLoot(boolean playing) {
        ItemStack item = new ItemStack(Material.STICK);

        for (int i = 0; i < 50; i++) {
            Collections.shuffle(loot);
            for (FishingLoot entry : loot) {
                if (!playing && entry.isPlayersOnly())
                    continue;
                if (entry.getChance() > ThreadLocalRandom.current().nextInt(100)) {
                    item = entry.getItem();
                }
            }
        }

        return item;
    }

    private void populateLootFromConfig() {
        Material[] fishes = {Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH};
        for (String s : lootTable) {
            System.out.println(s);
            String[] args = s.split(" ");
            int chance = Integer.parseInt(args[0]);
            boolean playersOnly = Boolean.parseBoolean(args[2]);
            Material mat = Material.getMaterial(args[1].toUpperCase());

            if (mat == null) {
                return;
            }
            ItemStack item = new ItemStack(mat);
            if (args.length > 3) {
                for (int i = 3; i < args.length; i++) {
                    System.out.println(args[i]);
                    if (mat == Material.POTION || mat == Material.SPLASH_POTION || mat == Material.LINGERING_POTION) {
                        PotionType potionType = PotionType.INSTANT_HEAL;
                        try {
                            potionType = PotionType.valueOf(args[i].toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Could not parse potion: " + s);
                        }
                        boolean extended = false;
                        boolean upgraded = false;
                        if (args.length > i + 1) {
                            if (args[i + 1].equalsIgnoreCase("extended"))
                                extended = true;
                            if (args[i + 1].equalsIgnoreCase("upgraded"))
                                upgraded = true;
                            if (extended || upgraded)
                                i++;
                        }
                        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                        potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
                        item.setItemMeta(potionMeta);
                    }
                    Enchantment enchant = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[i].toLowerCase()));
                    if (enchant != null) {
                        System.out.println("Enchant:" + enchant.getName());
                        int level = 1;
                        if (args.length > i + 1) {
                            i++;
                            try {
                                level = Integer.parseInt(args[i]);
                            } catch (NumberFormatException ignored) {
                                i--;
                            }
                        }
                        ItemMeta meta = item.getItemMeta();
                        meta.addEnchant(enchant, level, true);
                        item.setItemMeta(meta);
                    }
                }
            }
            loot.add(new FishingLoot(item, chance, playersOnly));
        }

        for (Material mat : fishes) {
            Fish fish = new Fish(mat, 1, 0, null);
            loot.add(new FishingLoot(fish.generateItem(), 10, false));
        }
    }

    @EventHandler
    public void onFishEvent(PlayerFishEvent event) {
        // handle fishing
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            PlayerManager pm = FSApi.getPlayerManager();

            ItemStack caught = getFishingLoot(pm.isPlaying(event.getPlayer()));

            if (event.getCaught() == null)
                return;

            if (event.getCaught() instanceof Item) {
                ((Item) event.getCaught()).setItemStack(caught);
            }
        }
    }

}

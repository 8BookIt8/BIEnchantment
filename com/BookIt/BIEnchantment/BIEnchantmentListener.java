package com.BookIt.BIEnchantment;

import com.BookIt.BIEnchantment.enchantments.BICustomEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BIEnchantmentListener implements Listener {
    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();

        ItemStack item = inv.getItem(0);
        ItemStack book = inv.getItem(1);
        ItemStack result = event.getResult();

        // none item
        if (item == null) {
            return ;
        }

        // item + none
        if (book == null && item.getType() == result.getType()) {
            Map<BICustomEnchantment, Integer> enchants = this.getBIEnchantments(item);
            ItemMeta meta = result.getItemMeta();
            enchants.forEach((enchant, lvl) -> {
                meta.addEnchant(enchant, lvl.intValue(), true);
            });

            result.setItemMeta(meta);
            return ;
        }

        // item + book or item + item
        if (book != null && (book.getType() == Material.ENCHANTED_BOOK || book.getType() == item.getType())) {
            Map<BICustomEnchantment, Integer> item_enchants = this.getBIEnchantments(item);
            Map<BICustomEnchantment, Integer> book_enchants = this.getEnchantables(item, book);
            item_enchants.forEach((enchant, lvl) -> {
                if (book_enchants.containsKey(enchant)) {
                    book_enchants.put(enchant, this.calculateLevel(enchant, lvl, book_enchants.get(enchant)));
                } else {
                    book_enchants.put(enchant, lvl);
                }
            });

            if (book_enchants.size() == 0) { return; }

            ItemStack new_result = (result.getType() != Material.AIR) ? new ItemStack(result) : new ItemStack(item);

            ItemMeta meta = new_result.getItemMeta();
            List<String> lore = new ArrayList<>();
            book_enchants.forEach((enchant, lvl) -> {
                meta.addEnchant(enchant, lvl.intValue(), true);
                lore.add(ChatColor.GRAY + enchant.getName() + " " + lvl);
            });

            meta.setLore(lore);
            new_result.setItemMeta(meta);

            event.setResult(new_result);
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("BIEnchantment");
            plugin.getServer().getScheduler().runTask(plugin, () -> inv.setRepairCost(1));
        }
    }

    private Map<BICustomEnchantment, Integer> getBIEnchantments(ItemStack item) {
        Map<Enchantment, Integer> enchants = item.getEnchantments();

        Map<BICustomEnchantment, Integer> BIenchants = new HashMap<>();
        enchants.forEach((enchant, lvl) -> {
            if (enchant instanceof BICustomEnchantment) {
                BIenchants.put((BICustomEnchantment) enchant, lvl);
            }
        });

        return BIenchants;
    }

    private Map<BICustomEnchantment, Integer> getEnchantables(ItemStack item, ItemStack book) {
        Map<BICustomEnchantment, Integer> enchants = this.getBIEnchantments(book);

        Map<BICustomEnchantment, Integer> new_enchants = new HashMap<BICustomEnchantment, Integer>();
        enchants.forEach((enchant, lvl) -> {
            if (item.getType() == Material.ENCHANTED_BOOK || enchant.canEnchantItem(item)) {
                new_enchants.put(enchant, lvl);
            }
        });

        return new_enchants;
    }

    private boolean isEnchantable(ItemStack item, ItemStack book) {
        Map<BICustomEnchantment, Integer> enchants = this.getEnchantables(item, book);
        if (enchants.size() == 0) {
            return false;
        }

        return true;
    }

    private int calculateLevel(BICustomEnchantment ench, int lvl1, int lvl2) {
        if (lvl1 == lvl2) {
            lvl1 += 1;
            return (lvl1 <= ench.getMaxLevel()) ? lvl1 : lvl2;
        } else {
            return (lvl1 >= lvl2) ? lvl1 : lvl2;
        }
    }
}

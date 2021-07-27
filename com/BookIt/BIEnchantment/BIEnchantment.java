package com.BookIt.BIEnchantment;

import com.BookIt.BIEnchantment.enchantments.EnchantmentThunder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BIEnchantment extends JavaPlugin implements Listener {
    public static final EnchantmentThunder THUNDER = new EnchantmentThunder("thunder");

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        this.getServer().getPluginManager().registerEvents(new BIEnchantmentListener(), this);

        this.registerEnchantments();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        item.addUnsafeEnchantment(BIEnchantment.THUNDER, 1);

        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Thunder I");
        meta.setLore(lore);
        item.setItemMeta(meta);

        player.getInventory().addItem(item);
    }

    private boolean isRegistered(Enchantment ench) {
        if (Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(ench)) {
            return true;
        }
        return false;
    }

    private void registerEnchantments(){
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        this.getServer().getPluginManager().registerEvents(THUNDER, this);

        if (!isRegistered(THUNDER)) {
            Enchantment.registerEnchantment(THUNDER);
        }

    }
}

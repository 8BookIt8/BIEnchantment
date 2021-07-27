package com.BookIt.BIEnchantment.enchantments;

import com.BookIt.BIEnchantment.BIItemType;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentThunder extends BICustomEnchantment implements Listener{
    public EnchantmentThunder(String key) {
        super(NamespacedKey.minecraft(key));
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            ItemStack item = ((Player) damager).getInventory().getItemInMainHand();
            if (item.getItemMeta().hasEnchant(this)) {
                Entity entity = event.getEntity();
                entity.getWorld().strikeLightning(entity.getLocation());
            }
        }
    }

    @Override
    public String getName() {
        return "Thunder";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return BIItemType.WEAPON.contains(itemStack.getType());
    }
}

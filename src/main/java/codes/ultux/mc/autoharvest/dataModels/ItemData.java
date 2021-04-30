package codes.ultux.mc.autoharvest.dataModels;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ItemData {
    private final Material itemType;
    private final int minAmount;
    private final int maxAmount;
    private final float chance;
    private Integer minLevel;
    private Integer maxLevel;
    private final List<Enchantment> enchantments;
    private final List<Biome> biomeList;
    private final String itemName;
    private final List<String> lore;

    public ItemData(Material itemType, int minAmount, int maxAmount, float chance, Integer minLevel, Integer maxLevel, List<Enchantment> enchantments, List<Biome> biomeList, String itemName, List<String> lore) {
        this.itemType = itemType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.chance = chance;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.enchantments = enchantments;
        this.biomeList = biomeList;
        this.itemName = itemName;
        this.lore = lore;
    }

    public Material getItemType() {
        return itemType;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public float getChance() {
        return chance;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }

    public List<Biome> getBiomeList() {
        return biomeList;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getLore() {
        return lore;
    }
}

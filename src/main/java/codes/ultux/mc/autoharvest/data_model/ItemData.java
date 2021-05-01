package codes.ultux.mc.autoharvest.data_model;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.HashMap;
import java.util.List;

public class ItemData {
    private final Material itemType;
    private final int minAmount;
    private final int maxAmount;
    private final float chance;
    private Integer minLevel;
    private Integer maxLevel;
    private final HashMap<Enchantment, Integer> enchantments;
    private final List<Biome> biomeList;
    private final String itemName;
    private final List<String> lore;
    private ItemStack generatedItem;

    public ItemData(Material itemType, int minAmount, int maxAmount, float chance, Integer minLevel, Integer maxLevel, HashMap<Enchantment, Integer> enchantments, List<Biome> biomeList, String itemName, List<String> lore) {
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
        generateItem();
    }

    private void generateItem(){
        generatedItem = new ItemStack(itemType);
        ItemMeta meta = generatedItem.getItemMeta();
        if (itemName != null) meta.setDisplayName(itemName);
        if (lore.size() > 0) meta.setLore(lore);
        if (enchantments.size() > 0)
            enchantments.forEach((enchantment, level) -> {
                meta.addEnchant(enchantment, level, true);
            });
        generatedItem.setItemMeta(meta);
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

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public List<Biome> getBiomeList() {
        return biomeList;
    }

    public String getItemName() {
        return itemName;
    }

    public ItemStack getGeneratedItem() {
        return generatedItem;
    }

    public List<String> getLore() {
        return lore;
    }
}

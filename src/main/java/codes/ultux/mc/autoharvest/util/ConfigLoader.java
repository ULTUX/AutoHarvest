package codes.ultux.mc.autoharvest.util;

import codes.ultux.mc.autoharvest.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ConfigLoader {
    private FileConfiguration configuration;
    File configFile;
    Logger logger;
    public ConfigLoader(File dataFolder, FileConfiguration configuration, Logger logger) {
        this.configuration = configuration;
        this.configFile = new File(dataFolder+File.separator+"config.yml");
        this.logger = logger;
    }

    public ConfigLoader(File dataFolder, String fileName, Logger logger) {
        File configFile = new File(dataFolder + File.separator + "lang.yml");
        this.logger = logger;
        if (!configFile.exists()){
            if (dataFolder.isDirectory()) dataFolder.mkdir();
            this.configuration = YamlConfiguration.loadConfiguration(configFile);
        }

    }

    public HashMap<ItemStack, Double> loadTreeItems(){
        ConfigurationSection treeDrop = configuration.getConfigurationSection("treeDrop");
        HashMap<ItemStack, Double> treeItems = new HashMap<>();

        if (treeDrop != null){
            treeDrop.getKeys(false).forEach(s -> {
                ConfigurationSection itemSection = treeDrop.getConfigurationSection(s);
                if (itemSection != null){
                    double chance = itemSection.getDouble("chance");
                    int min =  itemSection.getInt("min");
                    int max = itemSection.getInt("max");
                    String customName = itemSection.getString("customName");
                    Map<Enchantment, Integer> enchantments = getEnchantments(itemSection);
                    if (min < max && min > 0 && Material.getMaterial(s) != null){
                        ItemStack itemStack = new ItemStack(Material.getMaterial(s), ThreadLocalRandom.current().nextInt(min, max+1));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (customName != null) itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
                        if (enchantments != null) enchantments.forEach((enchantment, integer) -> itemMeta.addEnchant(enchantment, integer, true));
                        itemStack.setItemMeta(itemMeta);
                        treeItems.put(itemStack, chance);
                    }
                    else logger.warning("Config is invalid, some config elements may be skipped.");
                }
            });
        }
        return null;
    }

    private Map<Enchantment, Integer> getEnchantments(ConfigurationSection itemSection) {
        HashMap<Enchantment, Integer> enchantList = new HashMap<>();
        ConfigurationSection enchants = itemSection.getConfigurationSection("enchantments");
        if (enchants == null) return null;
        HashMap<String, Integer> enchantString = (HashMap) enchants.getValues(false);
        enchantString.forEach((s, integer) -> {
            enchantList.put(Enchantment.getByKey(NamespacedKey.minecraft(s)), integer);
        });
        return enchantList;
    }

}

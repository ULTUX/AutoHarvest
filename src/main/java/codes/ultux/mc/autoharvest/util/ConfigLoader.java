package codes.ultux.mc.autoharvest.util;

import codes.ultux.mc.autoharvest.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ConfigLoader {
    private FileConfiguration configuration;
    File configFile;
    Logger logger;
    public ConfigLoader(File dataFolder, FileConfiguration configuration, Logger logger) {
        this.configuration = configuration;
        this.configFile = new File(dataFolder + File.separator + "config.yml");
        this.logger = logger;
        if (!configFile.exists()) {
            generateConfig(dataFolder, configFile);
        }
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public ConfigLoader(File dataFolder, String fileName, Logger logger) {
        File configFile = new File(dataFolder + File.separator + "lang.yml");
        this.logger = logger;
        if (!configFile.exists()){
            generateConfig(dataFolder, configFile);
        }
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    private static void generateConfig(File parentFolder, File configFile){
        System.out.println("Config file does not exist, creating a new one...");
        if (!parentFolder.isDirectory()) parentFolder.mkdir();
        try (OutputStream outputStream = new FileOutputStream(configFile.toPath().toString())) {
            InputStream is = Main.instance.getResource("config.yml");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<ItemStack, HashMap<String, Double>> loadTreeItems(){
        ConfigurationSection treeDrop = configuration.getConfigurationSection("treeDrop");
        HashMap<ItemStack, HashMap<String, Double>> treeItems = new HashMap<>();

        if (treeDrop != null){
            treeDrop.getKeys(false).forEach(s -> {
                ConfigurationSection itemSection = treeDrop.getConfigurationSection(s);
                if (itemSection != null){
                    double chance = itemSection.getDouble("chance");
                    int min =  itemSection.getInt("min");
                    int max = itemSection.getInt("max");
                    String customName = itemSection.getString("custom-name");
                    Map<Enchantment, Integer> enchantments = getEnchantments(itemSection);
                    if (min < max && min > 0 && Material.getMaterial(s) != null){
                        ItemStack itemStack = new ItemStack(Material.getMaterial(s), ThreadLocalRandom.current().nextInt(min, max+1));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (customName != null) itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
                        if (enchantments != null) enchantments.forEach((enchantment, integer) -> itemMeta.addEnchant(enchantment, integer, true));
                        itemStack.setItemMeta(itemMeta);
                        HashMap<String, Double> data = new HashMap<>();
                        data.put("CHANCE", chance);
                        data.put("MIN", (double) min);
                        data.put("MAX", (double) max);
                        treeItems.put(itemStack, data);
                    }
                    else logger.warning("Config is invalid, some config elements may be skipped.");
                }
            });
            return treeItems;
        }
        return null;
    }

    private Map<Enchantment, Integer> getEnchantments(ConfigurationSection itemSection) {
        HashMap<Enchantment, Integer> enchantList = new HashMap<>();
        ConfigurationSection enchants = itemSection.getConfigurationSection("enchantments");
        if (enchants == null) return null;
        HashMap<String, Integer> enchantString = (HashMap) enchants.getValues(false);
        enchantString.forEach((s, integer) -> {
            s = s.toLowerCase();
            if (Enchantment.getByKey(NamespacedKey.minecraft(s)) != null && integer != null) enchantList.put(Enchantment.getByKey(NamespacedKey.minecraft(s)), integer);
        });

        return enchantList;
    }

}

package codes.ultux.mc.autoharvest.config_loader;

import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.data_model.ItemData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ItemDataLoader {
    private FileConfiguration configSection;
    private File configFile;
    private String itemsSectionName;
    private final Logger logger;
    private final ArrayList<ItemData> itemData = new ArrayList<>();

    public ItemDataLoader(File dataFolder, String configFileName, String itemsSectionName, Logger logger) throws IllegalAccessException {
        if (!dataFolder.isDirectory()) throw new IllegalAccessException("dataFolder should be a directory.");
        this.itemsSectionName = itemsSectionName;
        this.configFile = new File(dataFolder + File.separator + configFileName);
        this.logger = logger;
        if (!configFile.exists()) {
            generateConfig(dataFolder, configFile);
        }
        this.configSection = YamlConfiguration.loadConfiguration(configFile);
        loadItemData();
    }

    public ItemDataLoader(File dataFolder, FileConfiguration configuration, String itemsSectionName, Logger logger) throws IllegalAccessException {
        this(dataFolder, "config.yml", itemsSectionName, logger);
    }


    private static void generateConfig(File parentFolder, File configFile) {
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



    void loadItemData() {
        ConfigurationSection itemSection = configSection.getConfigurationSection(itemsSectionName);
        HashMap<ItemStack, HashMap<String, Double>> treeItems = new HashMap<>();
        if (itemSection != null) {
            itemSection.getKeys(false).forEach(s -> {
                ConfigurationSection item = itemSection.getConfigurationSection(s);
                if (item != null) {
                    String itemName = s;
                    Material itemType = null;
                    if ((itemType = Material.getMaterial(itemName)) == null) return;
                    int minAmount = 0;
                    int maxAmount = 0;
                    float chance = 0;
                    Integer minLevel = null;
                    Integer maxLevel = null;
                    HashMap<Enchantment, Integer> enchantments = new HashMap<>();
                    ArrayList<Biome> biomeList = new ArrayList<>();
                    String customName = null;
                    List<String> lore = new ArrayList<>();

                    if (item.isSet("minAmount") && item.isSet("maxAmount") && item.isSet("chance")) {
                        minAmount = item.getInt("minAmount");
                        maxAmount = item.getInt("maxAmount");
                        chance = (float) item.getDouble("chance");

                        if (item.isSet("biomes")){
                            List<String> biomeStringList = item.getStringList("biomes");
                            biomeStringList.forEach(s1 -> {
                                try {
                                    Biome biome = Biome.valueOf(s1.toUpperCase());
                                    biomeList.add(biome);
                                } catch (IllegalArgumentException | NullPointerException ignored){}
                            });
                        }
                        if ((customName = item.getString("customName")) != null){
                            customName = ChatColor.translateAlternateColorCodes('&', customName);
                        }
                        if (item.isSet("enchants")){
                            ConfigurationSection section = item.getConfigurationSection("enchants");
                            section.getKeys(false).forEach(s1 -> {
                                int level = section.getInt(s1);
                                if (level > 0){
                                    enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(s1)), level);
                                }

                            });
                        }
                        if (item.isSet("lore")){
                            List <String> loreList = item.getStringList("lore");
                            lore = loreList.stream().map(s1 -> ChatColor.translateAlternateColorCodes('&', s1)).collect(Collectors.toList());
                        }
                        ItemData createdItemData = new ItemData(itemType, minAmount, maxAmount, chance, minLevel, maxLevel, enchantments, biomeList, customName, lore);
                        itemData.add(createdItemData);
                    }


                }
            });
        }

    }

    public ArrayList<ItemData> getItemData() {
        return itemData;
    }

}

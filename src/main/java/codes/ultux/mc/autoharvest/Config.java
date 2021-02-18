package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.util.ConfigLoader;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Config {
    public HashMap<ItemStack, HashMap<String, Double>> treeDrops;
    private final ConfigLoader mainConfigLoader = new ConfigLoader(Main.instance.getDataFolder(), Main.instance.getConfig(), Main.instance.getLogger());

    public Config() {
        treeDrops = mainConfigLoader.loadTreeItems();
    }
}

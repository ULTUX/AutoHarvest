package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.config_loader.ConfigLoader;
import codes.ultux.mc.autoharvest.config_loader.ItemDataLoader;
import codes.ultux.mc.autoharvest.data_model.ItemData;
import java.util.ArrayList;

public class Config {
    public ArrayList<ItemData> treeDropItemData;
    private final ConfigLoader mainConfigLoader = new ConfigLoader(Main.instance.getDataFolder(), Main.instance.getConfig(), Main.instance.getLogger());
    private final ItemDataLoader itemDataConfigLoader = new ItemDataLoader(Main.instance.getDataFolder(), Main.instance.getConfig(), "items", Main.instance.getLogger());

    public Config() throws IllegalAccessException {
        treeDropItemData = itemDataConfigLoader.getItemData();
    }
}

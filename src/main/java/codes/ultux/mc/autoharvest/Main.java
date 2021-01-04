package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.Events.BlockClickEventListener;
import codes.ultux.mc.autoharvest.Events.TreeChopEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static JavaPlugin instance;
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        instance = this;
        registerEventListeners();
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new BlockClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new TreeChopEventListener(), this);
    }
}

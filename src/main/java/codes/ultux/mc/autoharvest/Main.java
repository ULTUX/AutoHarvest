package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.event_listener.Animation;
import codes.ultux.mc.autoharvest.event_listener.BlockClickEventListener;
import codes.ultux.mc.autoharvest.event_listener.TreeChopEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {
    public static JavaPlugin instance;
    private Listener[] listeners;
    public static Config config;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            config = new Config();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        listeners = new Listener[]{new Animation(), new BlockClickEventListener(), new TreeChopEventListener()};
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

}

package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.event.Animation;
import codes.ultux.mc.autoharvest.event.BlockClickEventListener;
import codes.ultux.mc.autoharvest.event.TreeChopEventListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.EventListener;

public class Main extends JavaPlugin {
    public static JavaPlugin instance;
    private final Listener[] listeners = {new Animation(), new BlockClickEventListener(), new TreeChopEventListener()};

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        instance = this;
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));


    }

}

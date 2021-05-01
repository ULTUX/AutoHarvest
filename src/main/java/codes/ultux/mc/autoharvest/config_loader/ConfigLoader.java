package codes.ultux.mc.autoharvest.config_loader;

import codes.ultux.mc.autoharvest.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.*;
import java.util.logging.Logger;

public class ConfigLoader {
    private FileConfiguration configuration;
    private File configFile;
    private Logger logger;

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
}

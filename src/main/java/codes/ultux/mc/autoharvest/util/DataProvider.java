package codes.ultux.mc.autoharvest.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class DataProvider {

    /**
     * Get all seeds and their suitable soil.
     * @return The HashMap containing all that data.
     */
    public static HashMap<Material, Material[]> preparePlantMap(){
        HashMap<Material, Material[]> plantMap = new HashMap();
        Material[] farmlandArray = new Material[]{Material.FARMLAND};
        ArrayList<Material> anyLog = new ArrayList<>();
        Arrays.stream(Material.values()).forEach(material -> {
            if (material.toString().contains("LOG")) anyLog.add(material);
        });
        plantMap.put(Material.WHEAT_SEEDS, farmlandArray);
        plantMap.put(Material.POTATO, farmlandArray);
        plantMap.put(Material.CARROT, farmlandArray);
        plantMap.put(Material.BEETROOT_SEEDS, farmlandArray);
        plantMap.put(Material.COCOA, anyLog.toArray(new Material[0]));
        plantMap.put(Material.NETHER_WART, new Material[]{Material.SOUL_SAND});
        return plantMap;
    }

    public static HashMap<Material, Material> getCropBlockEquivalents(){
        HashMap<Material, Material> plantItemAndBlockType = new HashMap<>();
        plantItemAndBlockType.put(Material.WHEAT_SEEDS, Material.WHEAT);
        plantItemAndBlockType.put(Material.POTATO, Material.POTATOES);
        plantItemAndBlockType.put(Material.CARROT, Material.CARROTS);
        plantItemAndBlockType.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        plantItemAndBlockType.put(Material.COCOA, Material.COCOA_BEANS);
        plantItemAndBlockType.put(Material.NETHER_WART, Material.NETHER_WART);
        return plantItemAndBlockType;
    }

    public static ArrayList<Material> getAllLogTypes(){
        ArrayList<Material> logTypes = new ArrayList<>();
        Arrays.stream(Material.values()).forEach(material -> {
            if (material.toString().contains("LOG")) logTypes.add(material);
        });
        return logTypes;
    }

    public static ArrayList<Material> getAllLeafTypes(){
        ArrayList<Material> leafTypes = new ArrayList<>();
        Arrays.stream(Material.values()).forEach(material -> {
            if (material.toString().contains("LEAVES")) leafTypes.add(material);
        });
        return leafTypes;
    }

    public static ArrayList<Material> getAllTreeGroundMaterials() {
        ArrayList<Material> groundMaterials = new ArrayList<>();
        groundMaterials.add(Material.DIRT);
        groundMaterials.add(Material.GRASS_BLOCK);
        groundMaterials.add(Material.GRASS_PATH);
        groundMaterials.add(Material.PODZOL);
        return groundMaterials;
    }
}

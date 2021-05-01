package codes.ultux.mc.autoharvest.util;

import codes.ultux.mc.autoharvest.Main;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class LootGenerator {

    public static ArrayList<ItemStack> getTreeLoot(Block block){
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        Main.config.treeDropItemData.forEach((itemData) -> {
            List<Biome> allowedBiomes = itemData.getBiomeList();
            AtomicBoolean isAccepted = new AtomicBoolean(allowedBiomes.size() == 0);
            allowedBiomes.forEach(biome -> {
                if (biome == block.getBiome()) isAccepted.set(true);
            });
            if (isAccepted.get()) {
                int min = itemData.getMinAmount();
                int max = itemData.getMaxAmount();
                double chance = itemData.getChance();
                ItemStack itemStack = itemData.getGeneratedItem();
                if (chance >= Math.random()) {
                    int dropAmount = ThreadLocalRandom.current().nextInt(min, max + 1);
                    itemStack.setAmount(dropAmount);
                    itemStacks.add(itemStack);
                }
            }
        });
        return itemStacks;
    }
}

package codes.ultux.mc.autoharvest.util;

import codes.ultux.mc.autoharvest.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LootGenerator {

    public static ArrayList<ItemStack> getTreeLoot(){
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        Main.config.treeDrops.forEach((itemStack, stringDoubleHashMap) -> {
            int min = stringDoubleHashMap.get("MIN").intValue();
            int max = stringDoubleHashMap.get("MAX").intValue();
            double chance = stringDoubleHashMap.get("CHANCE");
            if (chance >= Math.random()){
                int dropAmount = ThreadLocalRandom.current().nextInt(min, max+1);
                itemStack.setAmount(dropAmount);
                itemStacks.add(itemStack);
            }
        });
        return itemStacks;
    }
}

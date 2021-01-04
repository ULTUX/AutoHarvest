package codes.ultux.mc.autoharvest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;

public class Utils {

    /**
     * Get distance in XZ plane.
     * @param location First location.
     * @param location2 Second location.
     * @return The distance.
     */
    private static float getXZDistance(Location location, Location location2){
        return ((float)Math.sqrt(Math.pow(location.getX()-location2.getX(), 2) + Math.pow(location.getZ()-location2.getZ(), 2)));
    }

    /**
     * Check for surrounding blocks with given parameters at the block.
     * @param block Check if given block is a neighbour to any logs.
     * @param allowedTypes All allowed types.
     * @param notHere A collection of blocks excluded from search.
     * @return ArrayList of neighbour logs.
     */
    static ArrayList<Block> findNeighbourBlocks(Block block, Collection<Material> allowedTypes, Collection<Block> notHere){
        ArrayList<Block> blocks = new ArrayList<>();
        for (int xi = -1; xi < 2; xi++){
            for (int yi = 0; yi < 2; yi++){
                for (int zi = -1; zi < 2; zi++){
                    if (zi == 0 && xi == 0 && yi == 0) continue;
                    Block checkedBlock = block.getRelative(xi,yi,zi);
                    if (allowedTypes.contains(checkedBlock.getType()) && !notHere.contains(checkedBlock)){
                        if (block.getType().equals(checkedBlock.getType())) blocks.add(checkedBlock);
                    }
                }
            }
        }
        return blocks;
    }
}

package codes.ultux.mc.autoharvest.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;

public class TreeUtils {

    /**
     * Get distance in XZ plane.
     * @param location First location.
     * @param location2 Second location.
     * @return The distance.
     */
    public static float getXZDistance(Location location, Location location2){
        return ((float)Math.sqrt(Math.pow(location.getX()-location2.getX(), 2) + Math.pow(location.getZ()-location2.getZ(), 2)));
    }

    /**
     * Check for surrounding blocks with given parameters at the block.
     * @param block Check if given block is a neighbour to any logs.
     * @param allowedType The type to be checked for.
     * @param notHere A collection of blocks excluded from search.
     * @return ArrayList of neighbour logs.
     */
    public static ArrayList<Block> findNeighbourBlocks(Block block, Material allowedType, Collection<Block> notHere){
        ArrayList<Block> blocks = new ArrayList<>();
        for (int xi = -1; xi < 2; xi++){
            for (int yi = 0; yi < 2; yi++){
                for (int zi = -1; zi < 2; zi++){
                    if (zi == 0 && xi == 0 && yi == 0) continue;
                    Block checkedBlock = block.getRelative(xi,yi,zi);
                    if (allowedType.equals(checkedBlock.getType()) && !notHere.contains(checkedBlock))blocks.add(checkedBlock);
                }
            }
        }
        return blocks;
    }

    public static ArrayList<Block> findNeighbourLeaves(Block block, Material allowedType, Collection<Block> notHere, Collection<Block> logCollection){
        ArrayList<Block> blocks = new ArrayList<>();
        for (int xi = -3; xi < 4; xi++){
            for (int yi = 0; yi < 4; yi++){
                for (int zi = -3; zi < 4; zi++){
                    if (zi == 0 && xi == 0 && yi == 0) continue;
                    Block checkedBlock = block.getRelative(xi,yi,zi);
                    if (allowedType.equals(checkedBlock.getType()) && !notHere.contains(checkedBlock)) blocks.add(checkedBlock);
                }
                }
            }
        return blocks;
    }



    public static Material getCorrespondingLeafType(Material log){
        switch (log){
            case OAK_LOG: return Material.OAK_LEAVES;
            case BIRCH_LOG: return Material.BIRCH_LEAVES;
            case ACACIA_LOG: return Material.ACACIA_LEAVES;
            case DARK_OAK_LOG: return Material.DARK_OAK_LEAVES;
            case JUNGLE_LOG: return Material.JUNGLE_LEAVES;
            case SPRUCE_LOG: return Material.SPRUCE_LOG;
        }
        return null;
    }
}

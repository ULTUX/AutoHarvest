package codes.ultux.mc.autoharvest;

import org.bukkit.Location;
import org.bukkit.block.Block;

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
}

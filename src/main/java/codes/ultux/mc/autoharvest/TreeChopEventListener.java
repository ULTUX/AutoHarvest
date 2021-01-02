package codes.ultux.mc.autoharvest;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class TreeChopEventListener implements Listener {
    Logger logger = Main.instance.getLogger();


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block brokenBlock = event.getBlock();
        if (player.hasPermission("autoharvest.choptree") || player.isOp()){
            Tree choppedTree = Tree.getTree(event.getBlock());
            if (choppedTree != null){
                choppedTree.getLogs().forEach(block -> {
                    block.setType(Material.STONE);
                });
            }
        }
    }






}
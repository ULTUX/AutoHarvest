package codes.ultux.mc.autoharvest.event;

import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.Tree;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.logging.Logger;

public class TreeChopEventListener implements Listener {

    Logger logger = Main.instance.getLogger();


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autoharvest.choptree") || player.isOp()){
            Tree choppedTree = Tree.getTree(event.getBlock());

            if (choppedTree != null){
                new Animation(choppedTree.getLogs(), choppedTree.getLeaves(), event.getPlayer().getLocation());
            }
        }
    }

}
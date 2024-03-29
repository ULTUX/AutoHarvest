package codes.ultux.mc.autoharvest.event_listener;

import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.Tree;
import codes.ultux.mc.autoharvest.util.LootGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.logging.Logger;

public class TreeChopEventListener implements Listener {

    Logger logger;

    public TreeChopEventListener() {
        this.logger = Main.instance.getLogger();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autoharvest.choptree") || player.isOp()){
            Tree choppedTree = Tree.getTree(event.getBlock());
            if (choppedTree != null){

                new Animation(choppedTree, event.getPlayer().getLocation());
                LootGenerator.getTreeLoot(event.getBlock()).forEach(itemStack -> {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
                });
            }
        }
    }

}
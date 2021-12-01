package codes.ultux.mc.autoharvest.event_listener;

import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.Tree;
import codes.ultux.mc.autoharvest.util.LootGenerator;
import codes.ultux.mc.autoharvest.util.TreeUtils;
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
        if ((player.hasPermission("autoharvest.choptree") || player.isOp()) &&
                player.getInventory().getItemInMainHand().getType().toString().contains("AXE")){
            Tree choppedTree = Tree.getTree(event.getBlock());
            if (choppedTree != null){
                for (int i = 0; i < choppedTree.getLogs().size(); i++)
                    TreeUtils.damageTool(player);
                new Animation(choppedTree, event.getPlayer().getLocation());
                LootGenerator.getTreeLoot(event.getBlock()).forEach(itemStack -> event.getBlock().getWorld().
                        dropItemNaturally(event.getBlock().getLocation(), itemStack));


            }
        }
    }

}
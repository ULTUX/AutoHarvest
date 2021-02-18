package codes.ultux.mc.autoharvest.event;


import codes.ultux.mc.autoharvest.util.DataProvider;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class BlockClickEventListener implements Listener {
    // <Seed, Allowed blocks to be planted on>
    private static final HashMap<Material, Material[]> plantMap = DataProvider.preparePlantMap();
    private static final HashMap<Material, Material> plantItemAndBlockType = DataProvider.getCropBlockEquivalents();


    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null){
            if (event.getPlayer().hasPermission("autoharvest.harvest") || event.getPlayer().isOp()){
                Player player = event.getPlayer();
                if (plantMap.containsKey(player.getInventory().getItemInMainHand().getType())){
                    if (Arrays.stream(plantMap.get(player.getInventory().getItemInMainHand().getType())).anyMatch(material -> event.getClickedBlock().getRelative(0, -1, 0).getType().equals(material))){
                        if (event.getClickedBlock().getBlockData() instanceof Ageable){
                            Ageable plant = (Ageable)event.getClickedBlock().getBlockData();
                            if (plant.getAge() == plant.getMaximumAge()){
                                harvestAndPlant(event.getClickedBlock(), event.getPlayer().getInventory().getItemInMainHand());
                                player.playSound(event.getClickedBlock().getLocation(), Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
                                player.swingMainHand();
                            }
                        }
                    }
                }
            }
        }
    }



    private void harvestAndPlant(Block crop, ItemStack seed) {
        crop.breakNaturally();
        crop.setType(plantItemAndBlockType.get(seed.getType()));
        seed.setAmount(seed.getAmount() - 1);

    }

}

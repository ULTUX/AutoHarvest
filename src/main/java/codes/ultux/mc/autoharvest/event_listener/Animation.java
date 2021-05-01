package codes.ultux.mc.autoharvest.event_listener;

import codes.ultux.mc.autoharvest.Tree;
import codes.ultux.mc.autoharvest.data_model.DataProvider;
import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.util.TreeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Animation implements Listener {
    static ArrayList<FallingBlock> currentBlockAnimations = new ArrayList<>();
    ConcurrentLinkedQueue<Block> spawnQueue = new ConcurrentLinkedQueue<>();
    Tree tree;

    public Animation(Tree tree, Location referenceLocation) {
        this.tree = tree;
        animate(tree.getLogs(), tree.getLeaves(), referenceLocation);
    }

    public Animation() {
    }

    /**
     * Start animation
     * @param logs Logs that build this tree.
     * @param leaves Leaves that build this tree.
     * @param reference Reference location (location of block breaker).
     */
    public void animate(Collection<Block> logs, Collection<Block> leaves, Location reference) {
        Block[] logArray = logs.toArray(new Block[0]);
        Arrays.sort(logArray, Comparator.comparingInt(Block::getY));
        Block[] leafArray = (Block[]) leaves.toArray(new Block[0]);
        Arrays.sort(leafArray, Comparator.comparingInt(Block::getY));
        ArrayList<Block> tempArrayList = new ArrayList<>();
        tempArrayList.addAll(Arrays.asList(logArray));
        tempArrayList.addAll(Arrays.asList(leafArray));
        Block[] tempArray = tempArrayList.toArray(new Block[0]);
        Arrays.sort(tempArray, Comparator.comparingInt(Block::getY));
        spawnQueue.addAll(Arrays.asList(tempArray));
        startSpawnFBlocks(reference);
    }


    private void startSpawnFBlocks(Location reference) {
        Random random = new Random(24L);
        ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();
        int min = reference.getBlockY();
        Location trunkMiddleLocation = new Location(tree.getTrunkBlocks().getFirst().getWorld(), 0, 0, 0);
        tree.getTrunkBlocks().forEach(block -> {
            trunkMiddleLocation.add(TreeUtils.getMiddleLocation(block.getLocation()));
        });
        Location trunkMiddleLocation2 = TreeUtils.divideByScalar(trunkMiddleLocation, tree.getTrunkBlocks().size());
        trunkMiddleLocation2.setY(0);
        reference.setY(0);
        BukkitRunnable spawnLogTask = new BukkitRunnable() {
            @Override
            public void run() {
                boolean spawnLeaves = false;
                if (!spawnQueue.isEmpty()) {
                    int currentHeight = spawnQueue.peek() != null ? spawnQueue.peek().getY() : 0;
                    while (!spawnQueue.isEmpty() && spawnQueue.peek().getY() == currentHeight) {
                        Block block = spawnQueue.remove();
                        Vector individualVelocity = trunkMiddleLocation2.toVector().subtract(reference.toVector());
                        individualVelocity.normalize();
                        individualVelocity.multiply(0.1);
                        individualVelocity.multiply(new Vector(2*(1 + 1 +random.nextDouble()-0.5), 1 + 2*(random.nextDouble()-0.5), 1 + 2*(random.nextDouble()-0.5)));
                        Location spawnLocation = TreeUtils.getMiddleLocation(block.getLocation());
                        FallingBlock fBlock = block.getWorld().spawnFallingBlock(spawnLocation, block.getBlockData());
                        fallingBlocks.add(fBlock);
                        currentBlockAnimations.add(fBlock);
                        block.setType(Material.AIR);
                        fBlock.setDropItem(false);
                        fBlock.setVelocity(individualVelocity);
                    }
                } else this.cancel();
            }
        };
        BukkitRunnable animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                fallingBlocks.forEach(fallingBlock -> {
                    if (fallingBlock.isDead()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                fallingBlocks.remove(fallingBlock);
                            }
                        }.runTaskLater(Main.instance, 10);
                    }
                });
                if (fallingBlocks.isEmpty()) {
                    new BukkitRunnable() {
                        final BukkitRunnable task = this;
                        @Override
                        public void run() {
                            task.cancel();
                        }
                    }.runTaskLater(Main.instance, 200);
                    return;
                }
                fallingBlocks.forEach(fallingBlock -> {
                    fallingBlock.setVelocity(fallingBlock.getVelocity().add(new Vector(0, -0.3, 0)));
                });
            }
        };
        animationTask.runTaskTimer(Main.instance, 0, 1);
        spawnLogTask.runTaskTimer(Main.instance, 0, 1);
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockChange (EntityChangeBlockEvent event){
        if (event.getEntity() instanceof FallingBlock && currentBlockAnimations.contains(event.getEntity())) {
            currentBlockAnimations.remove(event.getEntity());
            event.setCancelled(true);

            if (DataProvider.getAllLeafTypes().contains(event.getTo())) {
                event.getBlock().getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_GRASS_BREAK, 0.1f, 0.8f);
                event.getBlock().getWorld().spawnParticle(Particle.BLOCK_CRACK, event.getBlock().getLocation().add(0.5, 0.5, 0.5), 20, 1, 0.1, 0.1, 0.1, event.getBlockData());
            } else if (DataProvider.getAllLogTypes().contains(event.getTo())) {
                event.getBlock().getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_WOOD_BREAK, 0.1f, 0.8f);
                event.getBlock().getWorld().spawnParticle(Particle.BLOCK_CRACK, event.getBlock().getLocation().add(0.5, 0.5, 0.5), 20, 1, 0.1, 0.1, 0.1, event.getBlockData());
            }

        }
        }

}

package codes.ultux.mc.autoharvest.event;

import codes.ultux.mc.autoharvest.util.DataProvider;
import codes.ultux.mc.autoharvest.Main;
import codes.ultux.mc.autoharvest.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Animation implements Listener {
    ArrayList<FallingBlock> currentBlockAnimations = new ArrayList<>();
    ConcurrentLinkedQueue<Block> logSpawnQueue = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Block> leafSpawnQueue = new ConcurrentLinkedQueue<>();

    public Animation(Collection<Block> logs, Collection<Block> leaves, Location referenceLocation) {
        animate(logs, leaves, referenceLocation);
    }

    public Animation() {
    }

    private void animate(Collection<Block> logs, Collection<Block> leaves, Location reference) {
        Block[] logArray = logs.toArray(new Block[0]);
        Arrays.sort(logArray, Comparator.comparingInt(Block::getY));
        Block[] leafArray = (Block[]) leaves.toArray(new Block[0]);
        Arrays.sort(leafArray, Comparator.comparingInt(Block::getY));
        logSpawnQueue.addAll(Arrays.asList(logArray));
        leafSpawnQueue.addAll(Arrays.asList(leafArray));
        startSpawnFBlocks(reference);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autoharvest.choptree") || player.isOp()) {
            Tree choppedTree = Tree.getTree(event.getBlock());

            if (choppedTree != null) {
                animate(choppedTree.getLogs(), choppedTree.getLeaves(), event.getPlayer().getLocation());
            }
        }
    }

    private void startSpawnFBlocks(Location reference) {
        Random random = new Random(24L);
        ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();

        BukkitRunnable spawnLogTask = new BukkitRunnable() {
            @Override
            public void run() {
                boolean spawnLeaves = false;
                if (!logSpawnQueue.isEmpty()) {
                    int min = logSpawnQueue.stream().min(Comparator.comparingInt(Block::getY)).get().getY();
                    for (int i = 0; i < 3; i++) {
                        if (logSpawnQueue.isEmpty()) break;
                        else {
                            Block block = logSpawnQueue.remove();
                            Vector individualVelocity = block.getLocation().toVector().subtract(reference.toVector().setY(block.getY()));
                            individualVelocity.add(new org.bukkit.util.Vector(0, 5, 0));
                            individualVelocity.multiply(new org.bukkit.util.Vector(1, 0.1, 1));
                            individualVelocity.normalize();
                            individualVelocity.multiply(new Vector(1 + Math.pow((block.getY() - min), 2), 1, 1 + Math.pow((block.getY() - min), 2)));
                            individualVelocity.multiply(0.05);
                            individualVelocity.multiply(new Vector(0.75 + random.nextDouble() / 2, 0.75 + random.nextDouble() / 2, 0.75 + random.nextDouble() / 2));
                            if (individualVelocity.length() > 0.8) individualVelocity.normalize().multiply(0.75);
                            individualVelocity.setY(0.1);
                            FallingBlock fBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                            fallingBlocks.add(fBlock);
                            currentBlockAnimations.add(fBlock);
                            block.setType(Material.AIR);
                            fBlock.setDropItem(false);
                            fBlock.setVelocity(individualVelocity);
                        }
                    }

                } else spawnLeaves = true;
                if (spawnLeaves) {
                    if (!leafSpawnQueue.isEmpty()) {
                        int min = leafSpawnQueue.stream().min(Comparator.comparingInt(Block::getY)).get().getY();
                        for (int i = 0; i < 3; i++) {
                            if (leafSpawnQueue.isEmpty()) {
                                this.cancel();
                            } else {
                                Block block = leafSpawnQueue.remove();
                                Vector individualVelocity = block.getLocation().toVector().subtract(reference.toVector().setY(block.getY()));
                                individualVelocity.add(new org.bukkit.util.Vector(0, 5, 0));
                                individualVelocity.multiply(new org.bukkit.util.Vector(1, 0.1, 1));
                                individualVelocity.normalize();
                                individualVelocity.multiply((block.getY() - min) * 4);
                                individualVelocity.multiply(new Vector(1 + Math.pow((block.getY() - min), 2), 1, 1 + Math.pow((block.getY() - min), 2)));
                                individualVelocity.multiply(new Vector(0.75 + random.nextDouble() / 2, 0.75 + random.nextDouble() / 2, 0.75 + random.nextDouble() / 2));
                                if (individualVelocity.length() > 0.8) individualVelocity.normalize().multiply(0.75);
                                individualVelocity.setY(0.1);
                                FallingBlock fBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                                currentBlockAnimations.add(fBlock);
                                fallingBlocks.add(fBlock);
                                fBlock.setVelocity(individualVelocity);
                                block.setType(Material.AIR);
                                fBlock.setDropItem(false);
                            }
                        }
                    }
                }
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
                            this.cancel();
                            return;
                        }
                        fallingBlocks.forEach(fallingBlock -> {
                            fallingBlock.setVelocity(fallingBlock.getVelocity().add(new Vector(0, -0.0035, 0)));
                        });
                    }
                };
                animationTask.runTaskTimer(Main.instance, 0, 2);

            }
        };
        spawnLogTask.runTaskTimer(Main.instance, 0, 2);
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

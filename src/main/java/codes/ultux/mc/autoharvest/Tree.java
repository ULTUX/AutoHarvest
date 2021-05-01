package codes.ultux.mc.autoharvest;

import codes.ultux.mc.autoharvest.exception.TreeNotFoundException;
import codes.ultux.mc.autoharvest.data_model.DataProvider;
import codes.ultux.mc.autoharvest.util.TreeUtils;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Leaves;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class Tree {
    static Logger logger = Main.instance.getLogger();

    /**
     * All log types
     */
    public static final ArrayList<Material> allLogTypes = DataProvider.getAllLogTypes();
    /**
     * All leaf materials.
     */
    private static final ArrayList<Material> allLeafTypes = DataProvider.getAllLeafTypes();
    /**
     * All materials that the tree can grow on.
     */
    private static final ArrayList<Material> groundMaterials = DataProvider.getAllTreeGroundMaterials();


    /**
     * All trunk base blocks of the tree
     */
    private final ConcurrentLinkedDeque<Block> trunkBlocks = new ConcurrentLinkedDeque<>();
    /**
     * All detected logs of the tree
     */
    private ConcurrentLinkedDeque<Block> logs = new ConcurrentLinkedDeque<>();
    /**
     * All detected leaves of the tree
     */
    private final ConcurrentLinkedDeque<Block> leaves = new ConcurrentLinkedDeque<>();

    /**
     * Try to find a tree at given block.
     */
    private Tree(Block block) throws TreeNotFoundException {
        if (allLogTypes.contains(block.getType())){
            getAllTrunkBlocks(block);
            searchForTree();
            findLeaves();
        }
        else throw new TreeNotFoundException("That trunk block is not a trunk.");
    }

    /**
     * Factory function, only way to get Tree instance.
     * @param block
     * @return
     */
    public static Tree getTree(Block block){
        try {
            return new Tree(block);
        } catch (TreeNotFoundException e) {
            logger.info(e.getMessage());
        }
        return null;
    }



    /**
     * Checks if block passed as arguments is a neighbour of any leaf block.
     * @param block Block to be checked.
     * @return Whether this block is a neighbour of any leaf.
     */
    private static boolean isLeafNeighbour(Block block){
        boolean isNeighbour = false;
        for (int xi = -1; xi < 2; xi++){
            for (int yi = -1; yi < 2; yi++){
                for (int zi = 0; zi < 2; zi++){
                    if (zi == 0 && yi == 0 && xi == 0) continue;
                    if (allLeafTypes.contains(block.getRelative(xi,yi,zi).getType()) && !((Leaves)block.getRelative(xi,yi,zi).getBlockData()).isPersistent()) isNeighbour = true;
                }
            }
        }
        return isNeighbour;
    }


    /**
     * Find all blocks that are a part of the tree. Max trunk size is 2x2, not more.
     * @param block Block that that is one of trunk blocks.
     */
    private void getAllTrunkBlocks(Block block) throws TreeNotFoundException{
        if (!DataProvider.getAllLogTypes().contains(block.getType())) throw new TreeNotFoundException("This is not tree trunk");
        if (!groundMaterials.contains(block.getRelative(0, -1, 0).getType())) throw new TreeNotFoundException("That tree is not growing on allowed surface.");
        Block diagonalTrunkBlock = null;
        int xi = -5, zi = -5;
        for (int x = -1; x < 2; x++){
            if (x == 0) continue;
            for (int z = -1; z < 2; z++){
                if (z == 0) continue;
                if (block.getRelative(x, 0, z).getType().equals(block.getType())){
                    diagonalTrunkBlock = block.getRelative(x, 0, z);
                    xi = x;
                    zi = z;
                    break;
                }
            }
            if (diagonalTrunkBlock != null) break;
        }

        if (diagonalTrunkBlock != null){
            logger.info("Found diagonal trunk block - the tree could be 2x2.");
            if (block.getRelative(0, 0, zi).getType().equals(block.getType()) && block.getRelative(xi, 0, 0).getType().equals(block.getType())){
                logger.info("This tree IS 2x2.");
                ArrayList<Block> possibleTrunkBlocks = new ArrayList<>();
                possibleTrunkBlocks.add(block.getRelative(0, 0, zi));
                possibleTrunkBlocks.add(block.getRelative(xi, 0, 0));
                possibleTrunkBlocks.add(block);
                possibleTrunkBlocks.add(diagonalTrunkBlock);

                //Check if there are any more logs surrounding the trunk

                for (Block block1 : possibleTrunkBlocks) {
                    for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            if (x == 0 && z == 0) continue;
                            if (!possibleTrunkBlocks.contains(block1.getRelative(x, 0, z)) && allLogTypes.contains(block1.getRelative(x, 0, z).getType()))
                                throw new TreeNotFoundException("That trunk has some more logs surrounding it! That's not how trees grow.");
                        }
                    }
                }
                trunkBlocks.addAll(possibleTrunkBlocks);

            }
            else {
                throw new TreeNotFoundException("This tree is not 2x2, but has other trunk diagonally to it. That's not a natural tree.");
            }
        }
        else {
            if (validateSmallTree(block)) trunkBlocks.add(block);
            else {
                throw new TreeNotFoundException("That tree has 1x1 trunk, but has some logs surrounding it.");
            }
        }
    }

    private static boolean validateSmallTree(Block block){
        for (int x = -1; x < 2; x++){
            for (int z = -1; z < 2; z++){
                if (z != 0 && x != 0 || x == 0 && z == 0) continue;
                if (allLogTypes.contains(block.getRelative(x, 0, z).getType())) return false;
            }
        }
        return true;
    }

    /**
     * Find remaining tree blocks
     */
    private void searchForTree() throws TreeNotFoundException {
        boolean hasLeaves = false;
        for (Block block : trunkBlocks){
            Block lastBlock = block;
            int y = 0;
            while (lastBlock.getType().equals(block.getType())){
                if (!block.getType().equals(Material.ACACIA_LOG) && y < 4 && !block.getRelative(0, y, 0).getType().equals(block.getType())) throw new TreeNotFoundException("That tree is too short to be a tree (min 5 blocks high).");
                Block followingBlock = lastBlock.getRelative(0, 1, 0);
                if (!((Orientable)lastBlock.getBlockData()).getAxis().equals(Axis.Y)) throw new TreeNotFoundException("One of tree main branches is rotated wrongly.");
                logs.add(lastBlock);
                if (followingBlock.getType().equals(block.getType())){
                    logs.addAll(TreeUtils.findNeighbourBlocks(followingBlock, block.getType(), logs));
                    if (isLeafNeighbour(followingBlock)) hasLeaves = true;
                }
                lastBlock = followingBlock;
                y++;
            }
        }
        if (!hasLeaves && !trunkBlocks.getFirst().getType().equals(Material.ACACIA_LOG)) throw new TreeNotFoundException("That tree's main branch does not touch any leaves. That's strange.");
        //Find the rest of tree's logs
        ConcurrentLinkedDeque<Block> queue = new ConcurrentLinkedDeque<>(logs);

        for (Block block : queue){
            ArrayList<Block> list = TreeUtils.findNeighbourBlocks(block, block.getType(), queue);
            queue.addAll(list);
        }
        logs = new ConcurrentLinkedDeque<>(queue);

    }

    private void findLeaves(){
        logs.forEach(block -> {
            ArrayList<Block> treeBlocks = new ArrayList<>(logs);
            treeBlocks.addAll(trunkBlocks);
            ArrayList<Block> possibleLeaves = TreeUtils.findNeighbourLeaves(block, TreeUtils.getCorrespondingLeafType(block.getType()), leaves, treeBlocks);
            leaves.addAll(possibleLeaves);
        });
    }

    public ConcurrentLinkedDeque<Block> getLeaves() {
        return leaves;
    }

    public ConcurrentLinkedDeque<Block> getLogs() {
        return logs;
    }

    public ConcurrentLinkedDeque<Block> getTrunkBlocks() {
        return trunkBlocks;
    }
}

package makmods.levelstorage.logic.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A pending replace for OreFinder.
 * 
 * @author mak326428
 * 
 */
public class AdvBlockFinder {
	private ArrayList<BlockPos> blocksFound = Lists.newArrayList();

	private int initialX;
	private int initialY;
	private int initialZ;

	private String targetName;
	private World world;

	/**
	 * Initializes a new instance of this class. <br />
	 * <b>WARNING: DO NOT USE THIS ON STONE OR <br />
	 * ANY OTHER MATERIAL LIKE STONE (IT USES RECURSION, IF YOU USE THIS ON <br />
	 * MATERIAL THAT THERE ARE MILLIONS OF IN THE WORLD, YOU MIGHT HAVE A LOT OF
	 * TROBLES <br />
	 * IF YOU USE THIS ON THAT MATERIAL (TESTED ON STONE), GAME WILL CRASH WITH
	 * STACK OVERFLOW!</b>
	 * 
	 * @param w
	 *            World to find blocks in
	 * @param x
	 *            X coordinate of the initial block
	 * @param y
	 *            Y coordinate of the initial block
	 * @param z
	 *            Z coordinate of the initial block
	 * @param targetName
	 *            OreDict-name of the target block. For instance, "oreTin",
	 *            "logWood"
	 */
	public AdvBlockFinder(World w, int x, int y, int z, String targetName) {
		this.initialX = x;
		this.initialY = y;
		this.initialZ = z;
		this.world = w;
		this.targetName = targetName;
		BlockPos initialBlock = new BlockPos(x, y, z);
		findContinuation(initialBlock);
	}

	public AdvBlockFinder(World w, BlockPos pos, String targetName) {
		this(w, pos.getX(), pos.getY(), pos.getZ(), targetName);
	}

	/**
	 * Quite self-descriptive.
	 * 
	 * @return
	 */
	public List<BlockPos> getBlocksFound() {
		return this.blocksFound;
	}

	private boolean isBlockOreDict(Block bl) {
		return bl != null && targetName != null ? OreDictionary.getOreName(
				OreDictionary.getOreIDs(new ItemStack(bl))[0]).equals(targetName)
				: false; // TODO: may consider more comprehensive solution
	}

	private void findContinuation(BlockPos loc) {
		boolean found = false;
		if (loc != null) {
			for (BlockPos locat : blocksFound) {
				if (locat.equals(loc))
					found = true;
			}
			if (!found)
				blocksFound.add(loc);
		}
		if (!found) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos newTh = loc.add(facing.getDirectionVec());
				Block currBlock = this.world.getBlockState(newTh).getBlock();
				if (currBlock != null) {
					if (isBlockOreDict(currBlock)) {
						// Recursion, very dangerous, but i hope nobody
						// will
						// use
						// this on stone...
						findContinuation(newTh);
					}
				}
			}
		}
	}
}

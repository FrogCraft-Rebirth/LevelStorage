package makmods.levelstorage.logic.util;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreFinder {
	public ArrayList<BlockPos> foundOre = new ArrayList<>();

	public int initialX;
	public int initialY;
	public int initialZ;

	//Use BlockState. So yah. 
	public IBlockState aimBlockState;

	// Unused, but what the heck, let it be here.
	public World world;

	public void calibrate(int x, int y, int z) {
		initialX = x;
		initialY = y;
		initialZ = z;
		BlockPos initialBlock = new BlockPos(x, y, z);
		// foundOre.add(initialBlock);
		findContinuation(initialBlock);
	}

	public void findContinuation(BlockPos loc) {
		boolean found = false;
		if (loc != null) {
			for (BlockPos locat : foundOre) {
				if (locat.equals(loc))
					found = true;
			}
			if (!found)
				foundOre.add(loc);
		}
		if (!found) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos newTh = loc.add(facing.getDirectionVec());
				IBlockState currBlock = this.world.getBlockState(new BlockPos(newTh));
				if (currBlock.equals(aimBlockState)) {
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

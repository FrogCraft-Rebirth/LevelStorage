package makmods.levelstorage.logic.util;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class OreFinder {
	public ArrayList<BlockLocation> foundOre = new ArrayList<BlockLocation>();

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
		BlockLocation initialBlock = new BlockLocation(world.provider.getDimension(), x, y, z);
		// foundOre.add(initialBlock);
		findContinuation(initialBlock);
	}

	public void findContinuation(BlockLocation loc) {
		boolean found = false;
		if (loc != null) {
			for (BlockLocation locat : foundOre) {
				if (locat.equals(loc))
					found = true;
			}
			if (!found)
				foundOre.add(loc);
		}
		if (!found) {
			for (int dir = 0; dir < 6; dir++) {
				BlockLocation newTh = loc.move(EnumFacing.values()[dir], 1);
				int currX = newTh.getX();
				int currY = newTh.getY();
				int currZ = newTh.getZ();

				IBlockState currBlock = this.world.getBlockState(new BlockPos(currX, currY, currZ));
				if (currBlock != null) {
					if (currBlock.equals(aimBlockState)) {
						IBlockState state = this.world.getBlockState(new BlockPos(currX, currY, currZ));
						// Recursion, very dangerous, but i hope nobody
						// will
						// use
						// this on stone...
						findContinuation(new BlockLocation(this.world.provider.getDimension(), currX, currY, currZ));
					}
				}
			}
		}
	}
}

package makmods.levelstorage.dimension.worldgen;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import makmods.levelstorage.LSBlockItemList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.oredict.OreDictionary;

public class WorldGeneratorPillar implements IWorldGenerator {

	public static List<IBlockState> toGenerate = Lists.newArrayList();

	static { //The reason of not expliciting support vanilla ore is that, ore dict has built-in support already
		String[] oreIDs = OreDictionary.getOreNames();
		for (String s : oreIDs) {
				if (s.startsWith("ore")) {
					List<ItemStack> stacksForGiven = OreDictionary.getOres(s);
					for (ItemStack st : stacksForGiven) {
						if (st.getItem() instanceof ItemBlock)
							toGenerate.add(Block.getBlockFromItem(st.getItem()).getDefaultState());
				}
			}
		}
	}
	
	public static int RARITY = 16;

	public static Block wrapper = LSBlockItemList.blockUnstableQuartz;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (random.nextInt(RARITY) == 0) {
			int genX = chunkX * 16 + random.nextInt(16);
			int genZ = chunkZ * 16 + random.nextInt(16);
			int genY = 0;
			for (int i = 256; i >= 0; i--) {
				if (!world.isAirBlock(new BlockPos(genX, i, genZ))) {
					genY = i;
					break;
				}
			}
			if (genY > 0) {
				int height = random.nextInt(150) + 80;
				int THICKNESS = (int) Math.ceil((float) height / (float) 40
						* (float) 3);
				for (int i = 0; i < height; i++) {
					for (int x = -(THICKNESS / 2) + 1; x < THICKNESS / 2; x++) {
						for (int z = -(THICKNESS / 2) + 1; z < THICKNESS / 2; z++) {
							int xT = genX + x;
							int yT = genY + i;
							int zT = genZ + z;
							IBlockState idToGenerate = toGenerate.get(random
									.nextInt(toGenerate.size()));
							world.setBlockState(new BlockPos(xT, yT, zT), idToGenerate);
						}
					}
					for (int x = -(THICKNESS / 2); x <= THICKNESS / 2; x++) {
						for (int z = -(THICKNESS / 2); z <= THICKNESS / 2; z++) {
							int xT = genX + x;
							int yT = genY + i;
							int zT = genZ + z;
							if (world.isAirBlock(new BlockPos(xT, yT, zT)))
								world.setBlockState(new BlockPos(xT, yT, zT), wrapper.getDefaultState());
						}
					}
				}
			}
		}
	}

}

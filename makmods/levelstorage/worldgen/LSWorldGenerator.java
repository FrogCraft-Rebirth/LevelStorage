package makmods.levelstorage.worldgen;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class LSWorldGenerator implements IWorldGenerator {

	public static LSWorldGenerator instance;
	public static int oreDensityFactor;
	public static float GENERATON_THRESHOLD = 10.0F;

	public static final String WORLDGEN_CATEGORY = "worldgen";

	public LSWorldGenerator() {
		oreDensityFactor = LSConfig.oreDensityFactor;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.isSurfaceWorld()) {
			final int chromiumOrePasses = 50 * oreDensityFactor;
			final int chromiumOreRarity = 100;
			for (int pass = 0; pass < chromiumOrePasses; pass++) {
				if (random.nextInt(chromiumOreRarity) == 0) {
					int xToGen = chunkX * 16 + random.nextInt(16);
					int zToGen = chunkZ * 16 + random.nextInt(16);
					int yToGen = random.nextInt(13) + 3;
					new WorldGenMinable(LSBlockItemList.blockChromiteOre.getStateFromMeta(3), 3)
							.generate(world, random, new BlockPos(xToGen, yToGen, zToGen));
				}
			}
		}
	}
}

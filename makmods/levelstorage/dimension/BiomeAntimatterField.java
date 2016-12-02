package makmods.levelstorage.dimension;

import java.util.Random;

import makmods.levelstorage.dimension.worldgen.WorldGeneratorAsteroids;
import makmods.levelstorage.dimension.worldgen.WorldGeneratorContinent;
import makmods.levelstorage.dimension.worldgen.WorldGeneratorPillar;
import makmods.levelstorage.dimension.worldgen.WorldGeneratorUUMFountain;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeAntimatterField extends Biome {

	public BiomeAntimatterField() {
		super(new BiomeProperties("Antimatter Field")
				.setWaterColor(16421912)
				.setRainDisabled()
				.setBaseHeight(0.15F)
				.setHeightVariation(0.05F));
		//setColor(16421912);
		//setBiomeName("Antimatter Field");
		//setDisableRain();
		//setTemperatureRainfall(2.0F, 0.0F);
		//setMinMaxHeight(0.1F, 0.2F);
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.topBlock = Blocks.AIR.getDefaultState();
        this.fillerBlock = Blocks.AIR.getDefaultState();
		BiomeDictionary.registerBiomeType(this, Type.PLAINS);
	}

	@Override
	public void decorate(World world, Random rand, BlockPos pos) {
		//You understand that the null is not allowed at all?!
		new WorldGeneratorPillar().generate(rand, pos.getX(), pos.getZ(), world, null, world.getChunkProvider());
		new WorldGeneratorAsteroids().generate(rand, pos.getX(), pos.getZ(), world, null, world.getChunkProvider());
		new WorldGeneratorUUMFountain().generate(rand, pos.getX(), pos.getZ(), world, null, world.getChunkProvider());
		new WorldGeneratorContinent().generate(rand, pos.getX(), pos.getZ(), world, null, world.getChunkProvider());
	}
}

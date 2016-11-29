package makmods.levelstorage.dimension;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
@Deprecated
// Copied from WolrdChunkManagerNether
// So use BiomeProviderSingle instead...
public class WorldChunkManagerAntimatterUniverse extends BiomeProvider
{
    /** this is the sole biome to utilize for this world */
    private Biome biomeToUse = CommonProxy.biomeAntimatterField;
    private float hellTemperature;

    /** The rainfall in the world */
    private float rainfall;
    
    public WorldChunkManagerAntimatterUniverse() {
        super();
    }
    
    public List<Biome> getBiomesToSpawnIn()
    {
        return Arrays.asList(biomeToUse);
    }

    /**
     * Returns the BiomeGenBase related to the x, z position on the world.
     */
    public Biome getBiomeGenAt(int x, int z)
    {
        return getBiome(new BlockPos(x, 0, z));
    }
    
    public Biome getBiome(BlockPos pos) {
    	return this.biomeToUse;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        Arrays.fill(biomes, 0, width * height, this.biomeToUse);
        return biomes;
    }

    /**
     * Returns a list of temperatures to use for the specified blocks.  Args: listToReuse, x, y, width, length
     */
    public float[] getTemperatures(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5)
    {
        if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5)
        {
            par1ArrayOfFloat = new float[par4 * par5];
        }

        Arrays.fill(par1ArrayOfFloat, 0, par4 * par5, this.hellTemperature);
        return par1ArrayOfFloat;
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    public float[] getRainfall(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5)
    {
        if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5)
        {
            par1ArrayOfFloat = new float[par4 * par5];
        }

        Arrays.fill(par1ArrayOfFloat, 0, par4 * par5, this.rainfall);
        return par1ArrayOfFloat;
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    public Biome[] loadBlockGeneratorData(Biome[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
    {
        if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5)
        {
            par1ArrayOfBiomeGenBase = new Biome[par4 * par5];
        }

        Arrays.fill(par1ArrayOfBiomeGenBase, 0, par4 * par5, this.biomeToUse);
        return par1ArrayOfBiomeGenBase;
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     */
    public Biome[] getBiomeGenAt(Biome[] biomes, int par2, int par3, int par4, int par5, boolean par6)
    {
        return this.loadBlockGeneratorData(biomes, par2, par3, par4, par5);
    }

    /**
     * Finds a valid position within a range, that is in one of the listed biomes. Searches {par1,par2} +-par3 blocks.
     * Strongly favors positive y positions.
     */
    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        return biomes.contains(this.biomeToUse) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> biomes)
    {
        return biomes.contains(this.biomeToUse);
    }
}

package makmods.levelstorage.dimension;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.dimension.worldgen.WorldGeneratorContinent;
import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AntimatterUniverseChunkGenerator implements IChunkGenerator {
	private World worldObj;
	private Random random;
	//No structure was used so far?
	//private final List<?> structureGenerators = new ArrayList<>();
	private final boolean hasDecoration;
	private final boolean hasDungeons;
	private WorldGenLakes waterLakeGenerator;
	private WorldGenLakes lavaLakeGenerator;
	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorOctaves noiseGen4;
	private NoiseGeneratorOctaves noiseGen5;

	private WorldGeneratorContinent[] otherContinents = new WorldGeneratorContinent[16];

	double[] noiseData1;
	double[] noiseData2;
	double[] noiseData3;
	double[] noiseData4;
	double[] noiseData5;
	private double[] densities;

	public AntimatterUniverseChunkGenerator(World par1World, long par2) {
		this.worldObj = par1World;
		this.random = new Random(par2);

		this.hasDecoration = true;
		noiseGen1 = new NoiseGeneratorOctaves(random, 16);
		noiseGen2 = new NoiseGeneratorOctaves(random, 16);
		noiseGen3 = new NoiseGeneratorOctaves(random, 8);
		noiseGen4 = new NoiseGeneratorOctaves(random, 10);
		noiseGen5 = new NoiseGeneratorOctaves(random, 16);

		for (int i = 0; i < otherContinents.length; i++)
			otherContinents[i] = new WorldGeneratorContinent();

		this.hasDungeons = false;
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int par1, int par2) {
		return this.provideChunk(par1, par2);
	}

	public static final IBlockState BLOCK_ID_TO_GENERATE = LSBlockItemList.blockAntimatterStone.getDefaultState();
	public static final int BLOCK_METADATA_TO_GENERATE = 0;

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it
	 * will generates all the blocks for the specified chunk from the map seed
	 * and chunk seed
	 */
	public Chunk provideChunk(int par1, int par2) {
		/*
		 * Chunk chunk = new Chunk(this.worldObj, par1, par2); byte b0 = 2; byte
		 * b1 = 33; this.densities = this.initializeNoiseField(this.densities,
		 * par1 * b0, 0, par2 * b0, b0 + 1, b1, b0 + 1); for (int k = 0; k < 63;
		 * ++k) { int l = k >> 4; ExtendedBlockStorage extendedblockstorage =
		 * chunk .getBlockStorageArray()[l];
		 * 
		 * if (extendedblockstorage == null) { extendedblockstorage = new
		 * ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
		 * chunk.getBlockStorageArray()[l] = extendedblockstorage; }
		 * 
		 * for (int i1 = 0; i1 < 16; ++i1) { for (int j1 = 0; j1 < 16; ++j1) {
		 * if (k == 0 || k == 1) { extendedblockstorage.setExtBlockID(i1, k &
		 * 15, j1, Block.bedrock.blockID); } else {
		 * 
		 * extendedblockstorage.setExtBlockID(i1, k & 15, j1,
		 * BLOCK_ID_TO_GENERATE); extendedblockstorage.setExtBlockMetadata(i1, k
		 * & 15, j1, BLOCK_METADATA_TO_GENERATE);
		 * 
		 * extendedblockstorage .setExtBlockID(i1, k & 15, j1, par4); } } } }
		 * chunk.generateSkylightMap(); return chunk;
		 */
		this.random.setSeed((long) par1 * 341873128712L + (long) par2
				* 132897987541L);
		int[] abyte = new int[32768];
		this.generateTerrain(par1, par2, abyte);
		Chunk chunk = createOkayChunk(worldObj, abyte, par1, par2);
		byte[] abyte1 = chunk.getBiomeArray();

		for (int k = 0; k < abyte1.length; ++k) {
			abyte1[k] = (byte) Biome.getIdForBiome(CommonProxy.biomeAntimatterField);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	public Chunk createOkayChunk(World w, int[] ids, int chunkX, int chunkZ) {
		Chunk chunk = new Chunk(w, chunkX, chunkZ);
		int k = ids.length / 256;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < k; ++y) {
					int idx = x << 11 | z << 7 | y;
					int id = ids[idx];
					if (id != 0) {
						int l = y >> 4;

						if (chunk.getBlockStorageArray()[l] == null) {
							chunk.getBlockStorageArray()[l] = new ExtendedBlockStorage(
									l << 4, !w.provider.getHasNoSky());
						}
						chunk.getBlockStorageArray()[l].set(x, y & 15, z, null);;
						//chunk.getBlockStorageArray()[l].setExtBlockID(x,
						//		y & 15, z, id);
					}
				}
			}
		}
		return chunk;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int par1, int par2) {
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	@Override
	public void populate(int par2, int par3) {
		BlockPos pos = new BlockPos(par2 * 16 + 16, 0, par3 * 16 + 16);
		Biome biomegenbase = this.worldObj.getBiomeForCoordsBody(pos);
		biomegenbase.decorate(this.worldObj, this.random, pos);
		/*new WorldGeneratorPillar().generate(random, par2, par3, this.worldObj, this, par1IChunkProvider);
		new WorldGeneratorAsteroids().generate(random, par2, par3, this.worldObj, this, par1IChunkProvider);
		new WorldGeneratorUUMFountain().generate(random, par2, par3, this.worldObj, this, par1IChunkProvider);
		new WorldGeneratorContinent().generate(random, par2, par3, this.worldObj, this, par1IChunkProvider);*/
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go. If
	 * passed false, save up to two chunks. Return true if all chunks have been
	 * saved.
	 */
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk. Not saved during autosave,
	 * only during world unload. Currently unimplemented.
	 */
	public void saveExtraData() {
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave() {
		return true;
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the
	 * given location.
	 */
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return Collections.emptyList();
	}

	/**
	 * Returns the location of the closest structure of the specified type. If
	 * not found returns null.
	 */
	public ChunkPosition findClosestStructure(World par1World, String par2Str,
			int par3, int par4, int par5) {
		return null;
	}

	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return true;
	}
	
	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}
	
	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		
	}

	public void generateTerrain(int par1, int par2, int[] par3ArrayOfByte) {
		byte b0 = 2;
		int k = b0 + 1;
		byte b1 = 33;
		int l = b0 + 1;
		this.densities = this.initializeNoiseField(this.densities, par1 * b0,
				0, par2 * b0, k, b1, l);

		for (int i1 = 0; i1 < b0; ++i1) {
			for (int j1 = 0; j1 < b0; ++j1) {
				for (int k1 = 0; k1 < 32; ++k1) {
					double d0 = 0.25D;
					double d1 = this.densities[((i1 + 0) * l + j1 + 0) * b1
							+ k1 + 0];
					double d2 = this.densities[((i1 + 0) * l + j1 + 1) * b1
							+ k1 + 0];
					double d3 = this.densities[((i1 + 1) * l + j1 + 0) * b1
							+ k1 + 0];
					double d4 = this.densities[((i1 + 1) * l + j1 + 1) * b1
							+ k1 + 0];
					double d5 = (this.densities[((i1 + 0) * l + j1 + 0) * b1
							+ k1 + 1] - d1)
							* d0;
					double d6 = (this.densities[((i1 + 0) * l + j1 + 1) * b1
							+ k1 + 1] - d2)
							* d0;
					double d7 = (this.densities[((i1 + 1) * l + j1 + 0) * b1
							+ k1 + 1] - d3)
							* d0;
					double d8 = (this.densities[((i1 + 1) * l + j1 + 1) * b1
							+ k1 + 1] - d4)
							* d0;

					for (int l1 = 0; l1 < 4; ++l1) {
						double d9 = 0.125D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i2 = 0; i2 < 8; ++i2) {
							int j2 = i2 + i1 * 8 << 11 | 0 + j1 * 8 << 7 | k1
									* 4 + l1;
							short short1 = 128;
							double d14 = 0.125D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 8; ++k2) {
								int l2 = 0;

								if (d15 > 0.0D) {
									l2 = LSBlockItemList.blockAntimatterStone.blockID;
								}

								par3ArrayOfByte[j2] = l2;
								j2 += short1;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2,
			int par3, int par4, int par5, int par6, int par7) {
		ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(
				this, par1ArrayOfDouble, par2, par3, par4, par5, par6, par7);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Event.Result.DENY)
			return event.noisefield;

		if (par1ArrayOfDouble == null) {
			par1ArrayOfDouble = new double[par5 * par6 * par7];
		}

		double d0 = 684.412D;
		double d1 = 684.412D;
		this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4,
				par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
		this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5,
				par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
		d0 *= 2.0D;
		this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1,
				par2, par3, par4, par5, par6, par7, d0 / 80.0D, d1 / 160.0D,
				d0 / 80.0D);
		this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2,
				par2, par3, par4, par5, par6, par7, d0, d1, d0);
		this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3,
				par2, par3, par4, par5, par6, par7, d0, d1, d0);
		int k1 = 0;
		int l1 = 0;

		for (int i2 = 0; i2 < par5; ++i2) {
			for (int j2 = 0; j2 < par7; ++j2) {
				double d2 = (this.noiseData4[l1] + 256.0D) / 512.0D;

				if (d2 > 1.0D) {
					d2 = 1.0D;
				}

				double d3 = this.noiseData5[l1] / 8000.0D;

				if (d3 < 0.0D) {
					d3 = -d3 * 0.3D;
				}

				d3 = d3 * 3.0D - 2.0D;
				float f = (float) (i2 + par2 - 0) / 1.0F;
				float f1 = (float) (j2 + par4 - 0) / 1.0F;
				float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

				if (f2 > 80.0F) {
					f2 = 80.0F;
				}

				if (f2 < -100.0F) {
					f2 = -100.0F;
				}

				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				d3 /= 8.0D;
				d3 = 0.0D;

				if (d2 < 0.0D) {
					d2 = 0.0D;
				}

				d2 += 0.5D;
				d3 = d3 * (double) par6 / 16.0D;
				++l1;
				double d4 = (double) par6 / 2.0D;

				for (int k2 = 0; k2 < par6; ++k2) {
					double d5 = 0.0D;
					double d6 = ((double) k2 - d4) * 8.0D / d2;

					if (d6 < 0.0D) {
						d6 *= -1.0D;
					}

					double d7 = this.noiseData2[k1] / 512.0D;
					double d8 = this.noiseData3[k1] / 512.0D;
					double d9 = (this.noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

					if (d9 < 0.0D) {
						d5 = d7;
					} else if (d9 > 1.0D) {
						d5 = d8;
					} else {
						d5 = d7 + (d8 - d7) * d9;
					}

					d5 -= 8.0D;
					d5 += (double) f2;
					byte b0 = 2;
					double d10;

					if (k2 > par6 / 2 - b0) {
						d10 = (double) ((float) (k2 - (par6 / 2 - b0)) / 64.0F);

						if (d10 < 0.0D) {
							d10 = 0.0D;
						}

						if (d10 > 1.0D) {
							d10 = 1.0D;
						}

						d5 = d5 * (1.0D - d10) + -3000.0D * d10;
					}

					b0 = 8;

					if (k2 < b0) {
						d10 = (double) ((float) (b0 - k2) / ((float) b0 - 1.0F));
						d5 = d5 * (1.0D - d10) + -30.0D * d10;
					}

					par1ArrayOfDouble[k1] = d5;
					++k1;
				}
			}
		}

		return par1ArrayOfDouble;
	}

}

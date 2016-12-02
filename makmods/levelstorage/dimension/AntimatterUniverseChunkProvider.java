package makmods.levelstorage.dimension;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class AntimatterUniverseChunkProvider implements IChunkProvider {

	private WorldServer world;
	private IChunkLoader chunkLoader;
	private IChunkGenerator chunkGen;
	
	public AntimatterUniverseChunkProvider(WorldServer world, IChunkLoader chunkLoader, IChunkGenerator chunkGen) {
		this.world = world;
		this.chunkLoader = chunkLoader;
		this.chunkGen = chunkGen;
	}
	
	@Override
	public Chunk getLoadedChunk(int x, int z) {
		return null;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to
	 * unload every such chunk.
	 */
	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	@Override
	public String makeString() {
		return "LSAntimatterUniverseChunkProvider";
	}

}

package makmods.levelstorage.dimension;

import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderAntimatterUniverse extends WorldProvider {
	
	public WorldProviderAntimatterUniverse() {
		//this.terrainType = WorldType.FLAT;
		this.biomeProvider = new BiomeProviderSingle(CommonProxy.biomeAntimatterField);
		//this.dimensionId = LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID;
	}
	
	public void registerWorldChunkManager()
	{
		this.biomeProvider = new BiomeProviderSingle(CommonProxy.biomeAntimatterField);
		//this.dimensionId = LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID;
	}
	
	@Override
	public String getWelcomeMessage() {
		return LSDimensions.getAntimatterUniverseDimName();
	}
	
    public BlockPos getSpawnPoint()
    {
    	return new BlockPos(0, 255, 9);
    }
	
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
    	return new Vec3d(0.0F, 0.0F, 0.0F);
    }
    
    @Override
    public Vec3d getFogColor(float par1, float par2)
    {
    	return new Vec3d(0.0F, 0.0F, 0.0F);
    }
	
    public IChunkGenerator createChunkGenerator()
    {
        return new AntimatterUniverseChunkProvider(worldObj, getSeed());
    }

	@Override
	public DimensionType getDimensionType() {
		return null;
	}
	
/*	@Override
	public boolean getWorldHasVoidParticles() {
		return true;
	}

	@Override
	public String getDimensionName() {
		return LSDimensions.getAntimatterUniverseDimName();
	}*/

}

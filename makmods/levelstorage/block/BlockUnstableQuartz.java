package makmods.levelstorage.block;

import makmods.levelstorage.LSCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BlockUnstableQuartz extends Block {

	public BlockUnstableQuartz(int id) {
		super(Material.ROCK);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(SoundType.STONE);
		this.setHardness(10.0F);
		this.setResistance(100.0F);
	}
	/*
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
    	this.blockIcon = par1IconRegister.registerIcon(ClientProxy.UNSTABLE_QUARTZ_TEXTURE);
    }*/

}

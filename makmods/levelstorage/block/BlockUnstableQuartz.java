package makmods.levelstorage.block;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUnstableQuartz extends Block {

	public BlockUnstableQuartz(int id) {
		super(id, Material.rock);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundStoneFootstep);
		this.setHardness(10.0F);
		this.setResistance(100.0F);
	}
	
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
    	this.blockIcon = par1IconRegister.registerIcon(ClientProxy.UNSTABLE_QUARTZ_TEXTURE);
    }

}

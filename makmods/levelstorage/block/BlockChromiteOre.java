package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

public class BlockChromiteOre extends Block {

	public BlockChromiteOre(int id) {
		super(Material.ROCK);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(SoundType.STONE);
		this.setHardness(3.0F);
		OreDictionary.registerOre("oreChromite", this);
	}

	public void addCraftingRecipe() {
		// --- No crafting recipe ---
	}

	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister
				.registerIcon(ClientProxy.CHROMITE_ORE_TEXTURE);
	}*/

}

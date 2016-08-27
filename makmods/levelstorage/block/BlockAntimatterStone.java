package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BlockAntimatterStone extends Block {

	public BlockAntimatterStone(int id) {
		super(Material.ROCK);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(/*Block.soundStoneFootstep*/SoundType.STONE);
		this.setHardness(6.0F);
		this.setResistance(600.0F);
		this.setLightLevel(1.0F);
	}

	/**
	 * The more number the less drop
	 */
	public static final int DROPRATE_RARITY = 500;

	public void addCraftingRecipe() {
		// --- No crafting recipe ---
	}

	public int quantityDropped(Random par1Random) {
		return par1Random.nextInt(DROPRATE_RARITY) == 0 ? 1 : 0;
	}
	
	public Item drop = OreDictionary.getOres("itemAntimatterMolecule").get(0).getItem();
	public int dropMeta = OreDictionary.getOres("itemAntimatterMolecule").get(0).getItemDamage();
	

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return drop;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return dropMeta;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister
				.registerIcon(ClientProxy.ANTIMATTER_STONE_TEXTURE);
	}*/

}

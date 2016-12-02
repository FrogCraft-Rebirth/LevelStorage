package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMassMelter extends BlockMachineStandart implements IHasRecipe {

	public BlockMassMelter(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.blockMassMelter), 
				"ata", "cic", "mem", 
				'm', IC2Items.getItem("te", "recycler"), 
				'e', IC2ItemsShortcut.ENERGY_CRYSTAL, 
				't', IC2Items.getItem("te", "teleporter"), 
				'a', IC2ItemsShortcut.ADV_MACHINE, 
				'c', IC2ItemsShortcut.ADV_CIRCUIT, 
				'i', IC2ItemsShortcut.IRIDIUM_PLATE);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMassMelter();
	}
}

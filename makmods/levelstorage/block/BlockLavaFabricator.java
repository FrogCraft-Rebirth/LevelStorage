package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLavaFabricator extends BlockMachineStandart implements IHasRecipe {

	public BlockLavaFabricator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.blockLavaFabricator), "aca", "pgp", 
				'a', IC2ItemsShortcut.ADV_CIRCUIT, 
				'p', IC2ItemsShortcut.ADV_ALLOY, 
				'c', IC2ItemsShortcut.ADV_MACHINE, 
				'g', IC2Items.getItem("te", "geo_generator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLavaFabricator();
	}
}

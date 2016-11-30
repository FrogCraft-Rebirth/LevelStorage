package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLavaFabricator extends BlockMachineStandart implements IHasRecipe {

	public BlockLavaFabricator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockLavaFabricator), "aca", "pgp", 
				Character.valueOf('a'), makmods.levelstorage.lib.IC2ItemsShortcut.ADV_CIRCUIT, 
				Character.valueOf('p'), makmods.levelstorage.lib.IC2ItemsShortcut.ADV_ALLOY, 
				Character.valueOf('c'), makmods.levelstorage.lib.IC2ItemsShortcut.ADV_MACHINE, 
				Character.valueOf('g'), IC2Items.getItem("geothermalGenerator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLavaFabricator();
	}
}

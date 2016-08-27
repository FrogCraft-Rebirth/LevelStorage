package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMassMelter extends BlockMachineStandart implements IHasRecipe {

	public BlockMassMelter(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockMassMelter), "ata", "cic", "mem", 'm',
				IC2Items.getItem("recycler"), 'e', IC2Items
						.getItem("energyCrystal"), 't', IC2Items
						.getItem("teleporter"), 'a', IC2Items
						.getItem("advancedMachine"), 'c', IC2Items
						.getItem("advancedCircuit"), 'i', IC2Items
						.getItem("iridiumPlate"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMassMelter();
	}
}

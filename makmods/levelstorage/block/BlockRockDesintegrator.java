package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.block.item.ItemBlockRockDesintegrator;
import makmods.levelstorage.init.CustomItemBlock;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@CustomItemBlock(itemBlock=ItemBlockRockDesintegrator.class)
public class BlockRockDesintegrator extends BlockMachineStandart implements IHasRecipe {

	public BlockRockDesintegrator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		//Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.blockRockDesintegrator), 
		//		"lll", "cmc", "www", 
		//		'l', new FluidStack(FluidRegistry.LAVA, 1000), 
		//		'w', new FluidStack(FluidRegistry.WATER, 1000),
		//		'c', IC2Items.getItem("crafting", "circuit"), 
		//		'm', IC2Items.getItem("machine"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRockDesintegrator();
	}

}

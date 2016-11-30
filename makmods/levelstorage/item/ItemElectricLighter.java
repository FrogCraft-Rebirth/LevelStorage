package makmods.levelstorage.item;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemElectricLighter extends ItemBasicElectric implements IHasRecipe {

	public ItemElectricLighter(int id) {
		super(id, 2, 100000, 1000, 500);
	}

	public boolean addFire(World world, BlockPos pos, EnumFacing sideHit) {
		boolean addedFire = false;
		BlockPos shifted = pos.add(sideHit.getDirectionVec()); //Vector addition
		if (!world.isAirBlock(shifted)) {
			return false;
		}
		world.setBlockState(shifted, Blocks.FIRE.getDefaultState());
		addedFire = true;
		return addedFire;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
				LSBlockItemList.itemElectricLighter),
				ic2.api.item.IC2Items.getItem("powerunitsmall").copy(), new ItemStack(
					net.minecraft.init.Items.FLINT_AND_STEEL), IC2ItemsShortcut.ADV_CIRCUIT);
	}

	@Override
	public boolean onBlockClick(ItemStack item, World world, EntityPlayer player, BlockPos pos, EnumFacing side) {
		if (world.isAirBlock(pos))
			return addFire(world, pos, side);
		
		IBlockState bs = world.getBlockState(pos);
		ItemStack smResult = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)));
		if (smResult == null)
			return addFire(world, pos, side);
		if (smResult.getItem() instanceof ItemBlock)
			world.setBlockState(pos, ((ItemBlock)smResult.getItem()).block.getDefaultState(), 3);
		else
			return addFire(world, pos, side);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemTexture() {
		return ClientProxy.ELECTRIC_LIGHTER_TEXTURE;
	}

	@Override
	public boolean onRightClick(ItemStack item, World world, EntityPlayer player) {
		return false;
	}

}

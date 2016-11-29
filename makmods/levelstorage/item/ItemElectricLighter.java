package makmods.levelstorage.item;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

	public boolean addFire(World world, int x, int y, int z, EnumFacing sideHit) {
		boolean addedFire = false;
		BlockLocation currBlockLocation = new BlockLocation(x, y, z);
		BlockLocation shifted = currBlockLocation.move(sideHit, 1);
		if (!world.isAirBlock(shifted.toBlockPos())) {
			return false;
		}
		world.setBlockState(shifted.toBlockPos(), Blocks.FIRE.getDefaultState());
		addedFire = true;
		return addedFire;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
				LSBlockItemList.itemElectricLighter),
				ic2.api.item.IC2Items.getItem("powerunitsmall").copy(), new ItemStack(
					net.minecraft.init.Items.FLINT_AND_STEEL), IC2Items.ADV_CIRCUIT);
	}

	@Override
	public boolean onBlockClick(ItemStack item, World world, EntityPlayer player, int x, int y, int z, int side) {
		BlockPos pos = new BlockPos(x, y, z);
		
		if (world.isAirBlock(pos))
			return addFire(world, x, y, z, side);
		
		IBlockState bs = world.getBlockState(pos);
		ItemStack smResult = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)));
		if (smResult == null)
			return addFire(world, x, y, z, side);
		if (smResult.getItem() instanceof ItemBlock)
			world.setBlockState(new BlockPos(x, y, z), ((ItemBlock)smResult.getItem()).block.getDefaultState(), 3);
		else
			return addFire(world, x, y, z, side);
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

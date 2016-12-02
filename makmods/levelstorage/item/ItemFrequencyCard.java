package makmods.levelstorage.item;

import java.util.List;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.logic.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class ItemFrequencyCard extends Item implements IHasRecipe {

	public ItemFrequencyCard(int id) {
		super();
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		// Frequency card
		ItemStack frequencyTr = IC2Items.getItem("frequency_transmitter");
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
		        LSBlockItemList.itemFreqCard), frequencyTr, new ItemStack(
				Items.PAPER));
		// To get rid of card data
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
		        LSBlockItemList.itemFreqCard), new ItemStack(
		        LSBlockItemList.itemFreqCard));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips, boolean adv) {
		NBTHelper.checkNBT(stack);
		if (stack.getTagCompound().hasKey("blockLocation")) { //BlockLocation has been entirely removed, so use magic string for a while
			boolean isValid = isValid(stack);
			BlockPos location = NBTHelper.getPositionData(stack.getTagCompound());
			tooltips.add(I18n.format("tooltip.freqCard.location") + " " + location);
			tooltips.add(I18n.format("tooltip.freqCard.isValid") + " " +
					(isValid ? I18n.format("other.true") : I18n.format("other.false")));
		}
	}

	//TODO: this method will not pass compile unless we find solution for missing dim id from BlockPos
	public static boolean isValid(ItemStack stack) {
		/*if (hasCardData(stack)) {
			BlockPos loc = NBTHelper.getPositionData(stack.getTagCompound());
			if (BlockLocation.isDimIdValid(loc.getDimId())) {
				WorldServer w = DimensionManager.getWorld(loc.getDimId());
				if (w.getBlockState(loc.toBlockPos()).getBlock() == LSBlockItemList.blockWlessConductor)
					return true;
			}
		}*/
		return false;
	}

	/**
	 * just if you want to easier get rid of invalid cards
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(net.minecraft.item.ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (itemStack != null) {
				if (player.isSneaking()) {
					NBTHelper.checkNBT(itemStack);
					if (!isValid(itemStack)) {
						itemStack = new ItemStack(LSBlockItemList.itemFreqCard);
						NBTHelper.checkNBT(itemStack);
					}
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}



	@Override //TODO: Replace BlockLocation with something else
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand,
									  EnumFacing facing, float hitX, float hitY, float hitZ) {
		/*if (!world.isRemote) {
			if (world.getBlockState(pos).getBlock() == LSBlockItemList.blockWlessConductor) {
				NBTHelper.checkNBT(stack);
				BlockLocation loc = new BlockLocation(world.provider.getDimension(), pos);
				BlockLocation.writeToNBT(stack.getTagCompound(), loc);
				return EnumActionResult.SUCCESS;
			}
		}*/
		return EnumActionResult.PASS;
	}

	public static boolean hasCardData(ItemStack stack) {
		NBTTagCompound cardNBT = stack.getTagCompound();
		return cardNBT.hasKey("blockLocation");
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.FREQUENCY_CARD_TEXTURE);
	}*/
}

package makmods.levelstorage.logic;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.item.ItemEnhancedDiamondDrill;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class DrillEnhancementRecipe implements IRecipe {

	public ItemStack getRes(InventoryCrafting inv) {
		boolean foundDrill = false;
		boolean foundBook = false;

		short enchantmentId = 0;
		short enchantmentLvl = 0;

		ItemStack drill = null;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if (currentStack != null) {
				if (currentStack.getItem() == Items.ENCHANTED_BOOK) {
					if (foundBook)
						return null;
					NBTTagList nbttaglist = currentStack.getTagCompound()
					        .getTagList("StoredEnchantments", 9);
					if (nbttaglist != null) {
						for (int j = 0; j < nbttaglist.tagCount(); ++j) {
							enchantmentId = ((NBTTagCompound) nbttaglist
							        .getCompoundTagAt(j)).getShort("id");
							enchantmentLvl = ((NBTTagCompound) nbttaglist
							        .getCompoundTagAt(j)).getShort("lvl");
						}
					}
					foundBook = true;
				}

				if (currentStack.getItem() == LSBlockItemList.itemEnhDiamondDrill) {
					if (foundDrill)
						return null;
					drill = currentStack;
					foundDrill = true;
				}
			}
		}

		if (foundDrill && foundBook && drill != null && enchantmentId > 0
		        && enchantmentLvl > 0) {
			if (drill.getTagCompound() == null)
				drill.setTagCompound(new NBTTagCompound());
			// if (drill.getTagCompound().getCompoundTag(
			// ItemEnhancedDiamondDrill.ENHANCEMENT_NBT) != null) {
			// return null;
			// }
			Enchantment ench = Enchantment.getEnchantmentByID(enchantmentId);
			if (ench == Enchantments.FORTUNE || ench == Enchantments.SILK_TOUCH) {
				NBTTagCompound enhNBT = new NBTTagCompound();
				enhNBT.setInteger(ItemEnhancedDiamondDrill.ENHANCEMENT_ID_NBT,
				        enchantmentId);
				enhNBT.setInteger(ItemEnhancedDiamondDrill.ENHANCEMENT_LVL_NBT,
				        enchantmentLvl);
				ItemStack newStack = drill.copy();
				newStack.getTagCompound().setTag(
				        ItemEnhancedDiamondDrill.ENHANCEMENT_NBT, enhNBT);
				return newStack;
			}
		}

		return null;
	}

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		return getRes(inventorycrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		return getRes(inventorycrafting);
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		return null;
	}

}

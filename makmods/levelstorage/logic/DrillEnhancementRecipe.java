package makmods.levelstorage.logic;

import java.util.Map;

import makmods.levelstorage.LSBlockItemList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class DrillEnhancementRecipe implements IRecipe {

	public ItemStack getRes(InventoryCrafting inv) {
		boolean foundDrill = false;
		boolean foundBook = false;

		Map<Enchantment, Integer> enchList = null;

		ItemStack drill = null;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if (currentStack != null) {
				if (currentStack.getItem() == Items.ENCHANTED_BOOK) {
					if (foundBook)
						return null;
					enchList = EnchantmentHelper.getEnchantments(currentStack);
					foundBook = enchList.size() > 0;
				}

				if (currentStack.getItem() == LSBlockItemList.itemEnhDiamondDrill) {
					if (foundDrill)
						return null;
					drill = currentStack;
					foundDrill = true;
				}
			}
		}

		if (foundDrill && foundBook && drill != null && enchList != null && enchList.size() > 0) {
			EnchantmentHelper.setEnchantments(enchList, drill);
			return drill;
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

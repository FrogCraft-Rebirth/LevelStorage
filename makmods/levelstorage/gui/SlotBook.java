package makmods.levelstorage.gui;

import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.item.ItemXPTome;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBook extends Slot {

	public SlotBook(IInventory par1IInventory, int par2, int par3, int par4) {
		super(par1IInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return checkItemValidity(stack);
	}

	public static boolean checkItemValidity(ItemStack stack) {
		for (XpStack s : XpStackRegistry.instance.entries) {
			if (s.stack.getItemDamage() == stack.getItemDamage()
			        && s.stack.getItem() == stack.getItem())
				return true;
		}

		if (stack.getItem() instanceof ItemXPTome)
			return true;

		return false;
	}
}

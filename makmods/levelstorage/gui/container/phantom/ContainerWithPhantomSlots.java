package makmods.levelstorage.gui.container.phantom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWithPhantomSlots extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotNum, int dragType, ClickType clickType,
			EntityPlayer player) {
		Slot slot = slotNum < 0 ? null : (Slot) this.inventorySlots
				.get(slotNum);
		if (slot instanceof PhantomSlot) {
			return slotClickPhantom(slot,dragType, clickType, player);
		}
		return super.slotClick(slotNum, dragType, clickType, player);
	}

	private ItemStack slotClickPhantom(Slot slot, int dragType,
			ClickType clickType, EntityPlayer player) {
		ItemStack stack = null;

		if (dragType == 2) {
			slot.putStack(null);
		} else if (dragType == 0 || dragType == 1) {
			InventoryPlayer playerInv = player.inventory;
			slot.onSlotChanged();
			ItemStack stackSlot = slot.getStack();
			ItemStack stackHeld = playerInv.getItemStack();

			if (stackSlot != null) {
				stack = stackSlot.copy();
			}

			// if (stackSlot == null) {
			if (stackHeld != null) {
				fillPhantomSlot(slot, stackHeld, dragType, clickType);
			}
			if (stackHeld == null) {
				// adjustPhantomSlot(slot, mouseButton, modifier);
				slot.putStack(null);
				slot.onPickupFromSlot(player, playerInv.getItemStack());
			}
		}
		return stack;
	}

	protected void fillPhantomSlot(Slot slot, ItemStack stackHeld,
			int dragType, ClickType clickType) {
		if (!slot.isItemValid(stackHeld))
			return;
		if (!(slot instanceof PhantomSlot))
			return;
		int stackSize;
		if (((PhantomSlot) slot).isUnstackable())
			stackSize = 1;
		else
			stackSize = dragType == 0 ? stackHeld.stackSize : 1;
		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}
		ItemStack phantomStack = stackHeld.copy();
		phantomStack.stackSize = stackSize;

		slot.putStack(phantomStack);
	}
}

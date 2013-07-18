package makmods.levelstorage.gui;

import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXpGenerator extends Container {
	protected TileEntityXpGenerator tileEntity;

	public ContainerXpGenerator(InventoryPlayer inventoryPlayer,
			TileEntityXpGenerator te) {
		tileEntity = te;
		addSlotToContainer(new SlotBook(tileEntity, 0, 80, 35));
		bindPlayerInventory(inventoryPlayer);
	}

	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
			int slotIndex) {

		ItemStack itemStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack slotItemStack = slot.getStack();
			itemStack = slotItemStack.copy();
			
			if (slotIndex < TileEntityXpGenerator.INVENTORY_SIZE) {
				// from the generator
				if (!this.mergeItemStack(slotItemStack,
						TileEntityXpGenerator.INVENTORY_SIZE,
						inventorySlots.size(), false)) {
					return null;
				}
			} else {
				// into the generator
				// WARNING: the following code is for this current case only. this won't work for you
				//if (!(slotItemStack.getItem() instanceof ItemLevelStorageBook))
				//	return null;
				if (!SlotBook.checkItemValidity(slotItemStack))
					return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
						TileEntityXpGenerator.INVENTORY_SIZE, false)) {
					return null;
				}
			}

			if (slotItemStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemStack;
	}
}

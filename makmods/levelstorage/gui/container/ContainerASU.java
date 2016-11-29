package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotArmor;
import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.tileentity.TileEntityASU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerASU extends Container {
	protected TileEntityASU tileEntity;

	public ContainerASU(InventoryPlayer inventoryPlayer, TileEntityASU te) {
		this.tileEntity = te;
		
		int height = 196;

		//This is wrong - EntityEquipmentSlot also has MAINHAND and OFFHAND
		//This is kept as this for convenience, and WILL be changed for accuracy
		for (EntityEquipmentSlot col : EntityEquipmentSlot.values()) {
			addSlotToContainer(new SlotArmor(inventoryPlayer, col, 8 + col.ordinal() * 18, 84));
		}

		addSlotToContainer(new SlotChecked(tileEntity, 0, 56, 17));
		addSlotToContainer(new SlotChecked(tileEntity, 1, 56, 53));
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9,
						8 + col * 18, height + -82 + row * 18));
			}
		}

		for (int col = 0; col < 9; col++)
			addSlotToContainer(new Slot(inventoryPlayer, col, 8 + col * 18, height + -24));
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		listeners.forEach(listener -> listener.sendProgressBarUpdate(this, 4, this.tileEntity.getStored()));
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 4: {
			tileEntity.setStored(value);
			break;
		}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
			int slotIndex) {
		return null;
	}
}

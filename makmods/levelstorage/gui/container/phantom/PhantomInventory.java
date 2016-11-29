package makmods.levelstorage.gui.container.phantom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public class PhantomInventory implements IInventory {

	public ItemStack[] phantomInventory;

	public PhantomInventory(int inventorySize) {
		super();
		phantomInventory = new ItemStack[inventorySize];
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return this.phantomInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.phantomInventory[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.phantomInventory[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				this.setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					this.setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null) {
			this.setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < this.phantomInventory.length; i++) {
			ItemStack stack = this.phantomInventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		par1NBTTagCompound.setTag("Inventory", itemList);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory", 9);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.get(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.phantomInventory.length) {
				this.phantomInventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}

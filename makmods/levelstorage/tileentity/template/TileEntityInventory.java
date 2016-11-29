package makmods.levelstorage.tileentity.template;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityInventory extends TileEntity implements IInventory {
	public ItemStack[] inv;

	public TileEntityInventory(int inventorySize) {
		super();
		inv = new ItemStack[inventorySize];
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public int getSizeInventory() {
		return this.inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.inv[slot] = stack;
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < this.inv.length; i++) {
			ItemStack stack = this.inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		par1NBTTagCompound.setTag("Inventory", itemList);
		return super.writeToNBT(par1NBTTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);

		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory", 9);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.get(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {}
}

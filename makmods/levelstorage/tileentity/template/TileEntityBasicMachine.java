package makmods.levelstorage.tileentity.template;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Maps;

public abstract class TileEntityBasicMachine extends TileEntityInventorySink {

	private int capacity;
	private int progress;
	private short facing = 0;

	private Map<Integer, ItemStack> slotMapping = Maps.newHashMap();
	private int rerenderInterval = 0;
	public boolean useSlotMappings;

	public TileEntityBasicMachine(int inventorySize, int capacity,
	        boolean useSlotMappings) {
		super(inventorySize);
		this.capacity = capacity;
		this.useSlotMappings = useSlotMappings;
	}

	public void addSlotMapping(int slotId, ItemStack stack) {
		slotMapping.put(slotId, stack.copy());
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return useSlotMappings ? itemstack.isItemEqual(inv[i]) : true;
	}

	public void addProgress(int amt) {
		this.progress += 1;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("progress", progress);
		par1NBTTagCompound.setShort("facing", facing);
		return super.writeToNBT(par1NBTTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.progress = par1NBTTagCompound.getInteger("progress");
		this.facing = par1NBTTagCompound.getShort("facing");
	}

	public abstract int getMaxProgress();

	/**
	 * Used in GUI
	 * 
	 * @param i
	 *            max pixels for capacitor
	 * @return Gauged scaled energy
	 */
	public int gaugeEnergyScaled(int i) {
		if (getStored() <= 0) {
			return 0;
		}
		int r = getStored() * i / getCapacity();
		if (r > i) {
			r = i;
		}
		return r;
	}
	
	public int gaugeProgressScaled(int i) {
		if (getProgress() <= 0) {
			return 0;
		}
		int r = getProgress() * i / getMaxProgress();
		if (r > i) {
			r = i;
		}
		return r;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	@Override
	public void update() {
		super.update();
		rerenderInterval++;
		if (rerenderInterval > 20) {
			worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
			rerenderInterval = 0;
		}
	}

}

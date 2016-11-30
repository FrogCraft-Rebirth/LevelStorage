package makmods.levelstorage.tileentity.template;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileEntityInventorySourceWithFluid extends
		TileEntityInventorySource implements IFluidHandler {
	public FluidTank tank;

	public TileEntityInventorySourceWithFluid(int maxOutput, int inventorySize, int tankSize) {
		super(maxOutput, inventorySize);
		tank = new FluidTank(tankSize * 1000);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.tank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.tank.writeToNBT(fluidTankTag);
		nbttagcompound.setTag("fluidTank", fluidTankTag);
		return super.writeToNBT(nbttagcompound);
	}

	public FluidTank getFluidTank() {
		return tank;
	}

	public int gaugeLiquidScaled(int i) {
		if (getFluidTank().getFluidAmount() <= 0)
			return 0;

		return getFluidTank().getFluidAmount() * i
				/ getFluidTank().getCapacity();
	}

	/* IFluidHandler */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource,
			boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

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

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}
}

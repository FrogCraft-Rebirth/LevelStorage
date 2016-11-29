package makmods.levelstorage.tileentity.template;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.network.packet.PacketReRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.PacketDispatcher;

import java.util.Arrays;
import java.util.List;


public abstract class TileEntityAdvanced extends TileEntity implements
		IEnergySource, IEnergyStorage, IEnergySink, IWrenchable, IInventory,
		IFluidHandler, ITickable {

	public ItemStack[] inv;
	public EnumFacing facing;
	public boolean addedToENet;

	public final boolean acceptsEnergy;
	public final boolean emitsEnergy;
	public final int maxStorage;
	public final int eut;
	public int stored;
	public final boolean energyNetTE;
	public final int tankVolume;
	public final boolean fluidTE;

	public FluidTank tank;

	public TileEntityAdvanced(int inventorySize, boolean energyNetTE,
			boolean acceptsEnergy, boolean emitsEnergy, int maxStorage,
			int eut, int tankVolume, boolean fluidTE) {
		super();
		this.inv = new ItemStack[inventorySize];
		this.acceptsEnergy = acceptsEnergy;
		this.emitsEnergy = emitsEnergy;
		this.maxStorage = maxStorage;
		this.eut = eut;
		this.energyNetTE = energyNetTE;
		this.tankVolume = tankVolume;
		this.fluidTE = fluidTE;
		tank = new FluidTank(tankVolume);
	}

	public boolean canUse(int amt) {
		return stored >= amt;
	}

	public void use(int amt) {
		stored -= amt;
	}

	public boolean useIfPossible(int amt) {
		if (canUse(amt)) {
			use(amt);
			return true;
		} else
			return false;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public boolean hasCustomName() {
		return true;
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
	public void update() {
		if (this.getWorld().isRemote)
			return;

		if (oldFacing != facing) {
			PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
			PacketReRender.reRenderBlock(xCoord, yCoord, zCoord);
			oldFacing = facing;
		}
		if (energyNetTE)
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				addedToENet = true;
			}
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < this.inv.length; i++) {
			ItemStack stack = this.inv[i];
			if (stack != null) {
				NBTTagCompound tagStack = new NBTTagCompound();
				tagStack.setByte("Slot", (byte) i);
				stack.writeToNBT(tagStack);
				itemList.appendTag(tagStack);
			}
		}
		tag.setTag("Inventory", itemList);
		tag.setInteger("facing", facing.ordinal());
		tag.setInteger("stored", stored);

		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.tank.writeToNBT(fluidTankTag);
		tag.setTag("fluidTank", fluidTankTag);
		return super.writeToNBT(tag);
	}

	@Override
	public void invalidate() {
		unloadFromENet();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unloadFromENet();
		super.onChunkUnload();
	}

	private void unloadFromENet() {
		if (energyNetTE)
			if (LevelStorage.isSimulating())
				if (addedToENet) {
					MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(
							this));
					addedToENet = false;
				}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tagCompound);
	}

	public EnumFacing oldFacing;

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList tagList = tag.getTagList("Inventory", 9);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tagSlot = (NBTTagCompound) tagList.get(i);
			byte slot = tagSlot.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tagSlot);
			}
		}

		facing = EnumFacing.VALUES[tag.getInteger("facing")];
		stored = tag.getInteger("stored");
		tank.readFromNBT(tag.getCompoundTag("fluidTank"));
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
		return emitsEnergy;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter,
			EnumFacing direction) {
		return acceptsEnergy;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
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

	@Override
	public IFluidTankProperties[] getTankProperties() {
		if (!fluidTE)
			return null;
		else
			return tank.getTankProperties();
	}

	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		return facing;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		this.facing = newDirection;
		return true;
	}

	@Override
	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return Arrays.asList(new ItemStack(this.getBlockType(), 1, this.getBlockMetadata()));
	}

	@Override
	public double getDemandedEnergy() {
		return Math.min(eut, maxStorage - stored);
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (stored + amount > maxStorage)
			return amount;
		this.stored += (int) amount;
		return 0;
	}

	@Override
	public int getStored() {
		return stored;
	}

	@Override
	public void setStored(int energy) {
		this.stored = energy;
	}

	@Override
	public int addEnergy(int amount) {
		this.stored += amount;
		return getStored();
	}

	@Override
	public int getCapacity() {
		return maxStorage;
	}

	@Override
	public int getOutput() {
		return 0;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(EnumFacing side) {
		return false;
	}

	@Override
	public double getOfferedEnergy() {
		if (!emitsEnergy)
			return 0;
		else
			return Math.min(stored, eut);
	}

	@Override
	public void drawEnergy(double amount) {
		this.stored -= amount;
	}
}

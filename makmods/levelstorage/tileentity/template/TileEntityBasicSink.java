package makmods.levelstorage.tileentity.template;

import java.util.Arrays;
import java.util.List;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LevelStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * More like an API, for basic sinks. Contains Energy Storage, Basic Energy
 * Inputs, Explosions on exceed of Max Packet Size, Wrenchable. If you intend on
 * using this, always use super.nameOfMethod()
 * 
 * @author mak326428
 * 
 */
public abstract class TileEntityBasicSink extends TileEntity implements
		ITickable, IEnergySink, IWrenchable, IEnergyStorage {

	private boolean addedToENet = false;
	private int stored;
	public static final String NBT_STORED = "stored";

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		stored = tag.getInteger(NBT_STORED);

	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger(NBT_STORED, stored);
		return super.writeToNBT(tag);
	}

	public boolean canUse(int amount) {
		return stored >= amount;
	}

	public void use(int amount) {
		stored -= amount;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(getPos(), this.getBlockMetadata(), tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void setStored(int energy) {
		this.stored = energy;
	}

	// IWrenchable stuff

	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		return null; //per ForgeDirection.UNKNOWN
	}

	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		return false;
	}

	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return Arrays.asList(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
	}

	// End of IWrenchable

	@Override
	public void update() {
		if (LevelStorage.isSimulating())
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				addedToENet = true;
			}
	}

	private void unloadFromENet() {
		if (LevelStorage.isSimulating())
			if (addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				addedToENet = false;
			}
	}

	public abstract void onUnloaded();

	@Override
	public void invalidate() {
		onUnloaded();
		unloadFromENet();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unloadFromENet();
		super.onChunkUnload();
	}

	public boolean isAddedToEnergyNet() {
		return addedToENet;
	}

	public abstract int getMaxInput();

	public abstract boolean explodes();

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
		return true;
	}

	@Override
	public int addEnergy(int amount) {

		this.stored += amount;
		return stored;
	}

	public abstract void onLoaded();

	public void validate() {
		super.validate();
		onLoaded();
	}

	public int getStored() {
		return this.stored;
	}

	public boolean isTeleporterCompatible(EnumFacing side) {
		return false;
	}

	@Override
	public double getDemandedEnergy() {
		return getCapacity() - getStored();
	}

	@Override //TODO: investigate that whether ic2 will handle explode or not
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (amount > getMaxInput() && explodes()) {
			this.invalidate();
			this.worldObj.setBlockToAir(this.getPos());
			this.worldObj.createExplosion(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 2F, false);
		}
		if ((this.getCapacity() - this.getStored()) > amount) {
			this.addEnergy((int) amount);
			return 0;
		} else {
			int leftover = (int) amount
					- (this.getCapacity() - this.getStored());
			this.setStored(getCapacity());
			return leftover;
		}
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return 0;
	}

	@Override
	public int getOutput() {
		return 0;
	}
	
	@Override
	public int getSinkTier() {
		return 1;
	}
}

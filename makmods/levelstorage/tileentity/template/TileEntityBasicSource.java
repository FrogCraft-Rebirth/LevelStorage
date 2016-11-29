package makmods.levelstorage.tileentity.template;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
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

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author mak326428
 * 
 */
public abstract class TileEntityBasicSource extends TileEntity implements
		ITickable, IEnergySource, IWrenchable, IEnergyStorage {

	private boolean addedToENet = false;
	private int stored;
	public int maxOutput;
	public static final String NBT_STORED = "stored";
	
	public TileEntityBasicSource(int maxOutput) {
		this.maxOutput = maxOutput;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		stored = par1NBTTagCompound.getInteger(NBT_STORED);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger(NBT_STORED, stored);
		return super.writeToNBT(par1NBTTagCompound);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tagCompound);
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
		if (!LevelStorage.isSimulating())
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

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
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

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(EnumFacing side) {
		return false;
	}

	@Override
	public int getOutput() {
		return 0;
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min(maxOutput, stored);
	}

	@Override
	public void drawEnergy(double amount) {
		stored -= amount;
	}
}

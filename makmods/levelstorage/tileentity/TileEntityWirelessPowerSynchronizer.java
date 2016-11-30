package makmods.levelstorage.tileentity;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.tileentity.template.IHasButtons;
import makmods.levelstorage.tileentity.template.IHasTextBoxes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements
		IHasTextBoxes, IHasButtons, ITickable, IEnergySink, IWrenchable,
		IEnergySource, IEnergyStorage {

	public static final int MAX_PACKET_SIZE = 2048;

	public static enum PowerSyncRegistry {
		
		INSTANCE;
		
		public List<TileEntityWirelessPowerSynchronizer> registry = Lists.newArrayList();
		
		private PowerSyncRegistry() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void tickStart(TickEvent.WorldTickEvent event) {
			if (event.phase == TickEvent.Phase.START)
				registry.clear();
		}

		@SubscribeEvent
		public void tickEnd(TickEvent.WorldTickEvent event) {
			//if (event.phase == TickEvent.Phase.END)
			// WChargerRegistry.instance.chargers.clear();
		}
	}

	public SyncType deviceType = SyncType.RECEIVER;
	public int frequency = 0;

	public int internalBuffer = 0;

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter,
			EnumFacing direction) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		if (this.deviceType == SyncType.RECEIVER)
			return Math.min(MAX_PACKET_SIZE, internalBuffer);
		else
			return 0;
	}

	@Override
	public void drawEnergy(double amount) {
		internalBuffer -= amount;
	}

	public void update() {
		if (!LevelStorage.isSimulating())
			return;
		PowerSyncRegistry.INSTANCE.registry.add(this);
		if (this.deviceType.equals(SyncType.RECEIVER))
			return;
	}

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
		return Arrays.asList(new ItemStack(LSBlockItemList.blockWlessPowerSync));
	}

	@Override
	public double getDemandedEnergy() {
		if (this.deviceType.equals(SyncType.RECEIVER)) {
			return 0;
		} else {
			int requiredForPairs = 0;
			List<TileEntityWirelessPowerSynchronizer> objS = Lists.newArrayList();
			for (TileEntityWirelessPowerSynchronizer pSync : PowerSyncRegistry.INSTANCE.registry) {
				if (pSync.frequency == this.frequency && pSync.deviceType.equals(SyncType.RECEIVER)) {
					objS.add(pSync);
				}
			}
			for (Object obj : objS) {
				if (obj instanceof TileEntityWirelessPowerSynchronizer) {
					TileEntityWirelessPowerSynchronizer te = (TileEntityWirelessPowerSynchronizer) obj;
					requiredForPairs += te.getCapacity() - te.getStored();
				}
			}
			return requiredForPairs;
		}
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (this.deviceType.equals(SyncType.RECEIVER))
			return amount;
		List<Object> mutableRightSyncList = Lists.newArrayList();
		for (TileEntityWirelessPowerSynchronizer pSync : PowerSyncRegistry.INSTANCE.registry) {
			if (pSync.frequency == this.frequency
					&& pSync.deviceType.equals(SyncType.RECEIVER)
					&& (pSync.getCapacity() - pSync.getStored() > 0)) {
				mutableRightSyncList.add(pSync);
			}
		}
		if (mutableRightSyncList.size() == 0)
			return amount;
		int forEach = (int) (amount / mutableRightSyncList.size());
		int notUsedUp = (int) amount;
		for (Object pSyncValid : mutableRightSyncList) {
			if (pSyncValid instanceof TileEntityWirelessPowerSynchronizer)
				((TileEntityWirelessPowerSynchronizer) pSyncValid)
						.addEnergy(forEach);
			notUsedUp -= forEach;
		}
		return notUsedUp;
	}

	@Override
	public void handleButtonClick(int buttonId) {
		this.deviceType = deviceType.getInverse();
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			frequency = Integer.parseInt(newText);
		} catch (Exception e) {
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.deviceType = tag.getBoolean("isTransmitter") ? SyncType.TRANSMITTER : SyncType.RECEIVER;
		this.frequency = tag.getInteger("frequency");
		this.internalBuffer = tag.getInteger("stored");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("isTransmitter", this.deviceType == SyncType.TRANSMITTER);
		tag.setInteger("frequency", frequency);
		tag.setInteger("stored", internalBuffer);
		return super.writeToNBT(tag);
	}

	@Override
	public int getStored() {
		return internalBuffer;
	}

	@Override
	public void setStored(int energy) {
		internalBuffer = energy;
	}

	@Override
	public int addEnergy(int amount) {
		internalBuffer += amount;
		return internalBuffer;
	}

	@Override
	public int getCapacity() {
		// max packet size + 1
		return 2048 + 1;
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
	public int getSourceTier() {
		return 4;
	}

	public void load() {
		if (!LevelStorage.isSimulating())
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	public void unload() {
		if (!LevelStorage.isSimulating())
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public void invalidate() {
		unload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unload();
		super.invalidate();
	}

	public void validate() {
		super.validate();
		load();
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

}

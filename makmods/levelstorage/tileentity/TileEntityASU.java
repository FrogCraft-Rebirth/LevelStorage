package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.client.GUIASU;
import makmods.levelstorage.gui.container.ContainerASU;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.network.packet.PacketReRender;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class TileEntityASU extends TileEntityInventory implements IWrenchable,
		IEnergySource, IEnergySink, IEnergyStorage, ITEHasGUI, ITickable {

	public static final long EU_STORAGE = 2000000000L;
	public static final int EU_PER_TICK = 8192;
	public static final int TIER = 5;
	public LogicSlot chargeSlot;
	public LogicSlot dischargeSlot;
	public EnumFacing oldFacing;
	private boolean addedToENet;

	public TileEntityASU() {
		super(2);
		chargeSlot = new LogicSlot(this, 0);
		dischargeSlot = new LogicSlot(this, 1);
	}

	public EnumFacing facing;
	public long stored;

	@Override
	public String getName() {
		return "ASU";
	}

	public void charge(LogicSlot ls, boolean charge) {
		if (ls.get() == null)
			return;
		if (!(ls.get().getItem() instanceof IElectricItem))
			return;
		IElectricItem electricItem = (IElectricItem) ls.get().getItem();
		if (charge) {
			this.stored -= ElectricItem.manager.charge(ls.get(),
					(int) Math.min(Integer.MAX_VALUE, stored), TIER, false,
					false);
		} else {
			if (!electricItem.canProvideEnergy(ls.get()))
				return;
			this.stored += ElectricItem.manager.discharge(ls.get(),
					(int) Math.min(Integer.MAX_VALUE, EU_STORAGE - stored),
					TIER, false, false, false);
		}
	}

	@Override
	public void update() {
		if (this.worldObj.isRemote)
			return;
		charge(chargeSlot, true);
		charge(dischargeSlot, false);
		if (oldFacing != facing) {
			PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
			PacketReRender.reRenderBlock(xCoord, yCoord, zCoord);
			oldFacing = facing;
		}
		if (!addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToENet = true;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(this.getPos(), 5, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
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

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem() instanceof IElectricItem;
	}

	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		return facing;
	}

	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		this.oldFacing = facing;
		this.facing = newDirection;
		return true;
	}

	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return Arrays.asList(new ItemStack(LSBlockItemList.blockASU, 1, state.getBlock().getMetaFromState(state)));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("facing", facing.ordinal());
		par1NBTTagCompound.setLong("stored", stored);
		return super.writeToNBT(par1NBTTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		facing = EnumFacing.VALUES[tag.getInteger("facing")];
		stored = tag.getLong("stored");
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
		return direction == facing;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
		return direction != facing;
	}

	@Override
	public int getSinkTier() {
		return TIER;
	}

	@Override
	public int getSourceTier() {
		return TIER;
	}

	@Override
	public int getStored() {
		return (int) Math.min(this.stored, (long) Integer.MAX_VALUE);
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
		return Integer.MAX_VALUE;
	}

	@Override
	public int getOutput() {
		return EU_PER_TICK;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return EU_PER_TICK;
	}

	@Override
	public boolean isTeleporterCompatible(EnumFacing side) {
		return side == facing;
	}

	private void unloadFromENet() {
		if (LevelStorage.isSimulating())
			if (addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				addedToENet = false;
			}
	}

	@Override
	public double getDemandedEnergy() {
		return EU_STORAGE - stored;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (this.stored > EU_STORAGE)
			return amount;
		this.stored += (long) amount;
		return 0;
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min((long) EU_PER_TICK, stored);
	}

	@Override
	public void drawEnergy(double amount) {
		stored -= amount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIASU(player.inventory, this);
	}

	public float getChargeLevel() {
		float ret = (float) stored / EU_STORAGE;
		if (ret > 1.0F)
			ret = 1.0F;

		return ret;
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerASU(player.inventory, this);
	}

}

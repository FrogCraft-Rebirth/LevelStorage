package makmods.levelstorage.tileentity;

import java.util.Arrays;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.gui.client.GUILavaFabricator;
import makmods.levelstorage.gui.container.ContainerLavaFabricator;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventorySinkWithFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLavaFabricator extends TileEntityInventorySinkWithFluid
		implements ITEHasGUI, ISidedInventory {

	public static final int EU_PER_LAVA_MB = 35; // multiply by thousand and get amount of EU needed for 1 bucket

	public LogicSlot fluidInput;
	public LogicSlot fluidOutput;

	public TileEntityLavaFabricator() {
		super(2, 16);
		this.fluidInput = new LogicSlot(this, 0);
		this.fluidOutput = new LogicSlot(this, 1);
	}

	@Override
	public String getName() {
		return "Lava Fabricator";
	}

	public boolean isValidInputInSlot(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		ItemStack copy = itemstack.copy();
		ItemStack filled = FluidContainerRegistry.fillFluidContainer(
				new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME), copy);
		return filled != null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0)
			return isValidInputInSlot(itemstack);
		return false;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return Arrays.asList(new ItemStack(LSBlockItemList.blockLavaFabricator));
	}

	@Override
	public int getCapacity() {
		return 32768;
	}

	@Override
	public void onUnloaded() {
	}

	@Override
	public int getMaxInput() {
		return 512;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	@Override
	public void onLoaded() {
		;
	}
	
	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	public void fillContainerIfPossible() {
		if (this.fluidInput.get() == null)
			return;
		ItemStack copy = this.fluidInput.get().copy();
		ItemStack filled = FluidContainerRegistry.fillFluidContainer(
				new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME), copy);
		if (this.tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME)
			if (fluidOutput.add(filled, true)) {
				fluidInput.consume(1);
				this.tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
				fluidOutput.add(filled, false);
			}
	}

	@Override
	public void update() {
		super.update();
		this.fillContainerIfPossible();
		while (canUse(EU_PER_LAVA_MB)) {
			boolean used = false;
			if (this.tank.getCapacity() - this.tank.getFluidAmount() >= 1) {
				this.tank.setFluid(new FluidStack(FluidRegistry.LAVA, this.tank
						.getFluidAmount() + 1));
				used = true;
			}
			if (!used)
				break;
			else
				use(EU_PER_LAVA_MB);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUILavaFabricator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerLavaFabricator(player.inventory, this);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing) {
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing) {
		if (slot == 1)
			return true;
		else
			return false;
	}

}

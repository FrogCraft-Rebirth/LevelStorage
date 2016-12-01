package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class BlockMachineStandart extends BlockContainer {

	public BlockMachineStandart(int par1) {
		super(Material.IRON);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(SoundType.METAL);
		this.setHardness(3.0F);
	}

	//protected Icon[] icons = new Icon[6];

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, 
			EntityPlayer player, EnumHand hand, ItemStack heldItem, 
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(pos) instanceof ITEHasGUI)
			return CommonHelper.handleMachineRightclick(world, pos.getX(), 
					pos.getY(), pos.getZ(), player);
		else
			return false;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		return icons[side];
	}*/

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.dropItems(world, pos);
		super.breakBlock(world, pos, state);
	}

	private void dropItems(World world, BlockPos pos) {
		Random rand = new Random();

		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof IInventory))
			return;
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i).copy();

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, pos.getX() + rx, 
						pos.getY() + ry, pos.getZ() + rz, item);

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound(
							(NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String blockName = this.getUnlocalizedName().replace("tile.", "");
		for (int i = 0; i < 6; i++) {
			String sname = EnumFacing.VALID_DIRECTIONS[i].name()
					.toLowerCase();
			icons[i] = iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(blockName + "/" + sname));
		}
	}*/

}

package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.network.packet.PacketReRender;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityASU;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockASU extends BlockContainer {
	public BlockASU(int id) {
		super(id, Material.iron);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityASU();
	}

	private Icon[] icons = new Icon[6];
	private Icon facing;

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return CommonHelper.handleMachineRightclick(world, x, y, z, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		return side != ForgeDirection.UP.ordinal() ? icons[side] : facing;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		this.dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory))
			return;
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z
						+ rz, new ItemStack(item.itemID, item.stackSize,
						item.getItemDamage()));

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
	
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
		TileEntity te = par1IBlockAccess.getBlockTileEntity(x, y, z);
		if (te == null || !(te instanceof TileEntityASU))
			return null;
		TileEntityASU asu = (TileEntityASU)te;
        return asu.getFacing() == side ? facing : icons[side];
    }

	public void onBlockPlacedBy(World world, int i, int j, int k,
			EntityLivingBase entityliving, ItemStack itemStack) {
		if (world.isRemote)
			return;

		TileEntityASU te = (TileEntityASU) world
				.getBlockTileEntity(i, j, k);

		if (entityliving == null) {
			te.setFacing((short) 1);
		} else {
			int yaw = MathHelper
					.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
			int pitch = Math.round(entityliving.rotationPitch);

			if (pitch >= 65)
				te.setFacing((short) 1);
			else if (pitch <= -65)
				te.setFacing((short) 0);
			else
				switch (yaw) {
				case 0:
					te.setFacing((short) 2);
					break;
				case 1:
					te.setFacing((short) 5);
					break;
				case 2:
					te.setFacing((short) 3);
					break;
				case 3:
					te.setFacing((short) 4);
				}
		}
		PacketDispatcher.sendPacketToAllPlayers(te.getDescriptionPacket());
		PacketReRender.reRenderBlock(te.xCoord, te.yCoord, te.zCoord);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String blockName = this.getUnlocalizedName().replace("tile.", "");
		for (int i = 0; i < 6; i++) {
			String sname = ForgeDirection.VALID_DIRECTIONS[i].name()
					.toLowerCase();
			icons[i] = iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(blockName + "/" + sname));
		}
		facing = iconRegister.registerIcon(ClientProxy
				.getTexturePathFor(blockName + "/" + "facing"));
	}

}
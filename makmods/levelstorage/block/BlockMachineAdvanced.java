package makmods.levelstorage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * BlockMachineAdvanced - a standart machine with facing
 * 
 * @author mak326428
 * 
 */
public abstract class BlockMachineAdvanced extends BlockMachineStandart {

	public BlockMachineAdvanced(int par1) {
		super(par1);
	}

	/*private Icon facing;

	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y,
			int z, int side) {
		TileEntity te = par1IBlockAccess.getBlockTileEntity(x, y, z);
		if (te == null || !(te instanceof IWrenchable))
			return null;
		IWrenchable asu = (IWrenchable) te;
		return asu.getFacing() == side ? facing : icons[side];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		return side != EnumFacing.SOUTH.ordinal() ? icons[side] : facing;
	}*/

	public void onBlockPlacedBy(World world, BlockPos pos, 
			IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (world.isRemote)
			return;
/*	TODO: Implement IWrenchable on block
		IWrenchable te = (IWrenchable) world.getBlockTileEntity(i, j, k);

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
		TileEntity tile = (TileEntity)te;
		// tell the client that machine changed facing
		PacketDispatcher.sendPacketToAllPlayers(tile.getDescriptionPacket());
		// and ask it to rerender the block
		PacketReRender.reRenderBlock(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());*/
	}/*

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		String blockName = this.getUnlocalizedName().replace("tile.", "");
		this.facing = iconRegister.registerIcon(ClientProxy.getTexturePathFor(blockName + "/"
				+ "facing"));
	}*/

}

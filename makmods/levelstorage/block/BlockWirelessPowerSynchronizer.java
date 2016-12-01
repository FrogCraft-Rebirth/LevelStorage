package makmods.levelstorage.block;

import java.util.Random;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWirelessPowerSynchronizer extends BlockContainer implements IHasRecipe {

	public BlockWirelessPowerSynchronizer(int id) {
		super(Material.IRON);
		this.setCreativeTab(LSCreativeTab.instance);
		this.setSoundType(SoundType.METAL);
		this.setHardness(3.0F);
	}
	/*
	 * private Icon down; private Icon up; private Icon side;
	 */

	@Override
	public void addCraftingRecipe() {
		ItemStack sync = new ItemStack(LSBlockItemList.blockWlessPowerSync, 4);
		IC2ItemsShortcut.class.getClass();
		Recipes.advRecipes.addRecipe(sync, "ccc", "ama", "ccc", 'a', IC2ItemsShortcut.ADV_CIRCUIT, 'c',
				new ItemStack(LSBlockItemList.blockWlessConductor), 'm', IC2ItemsShortcut.ADV_MACHINE);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return IC2ItemsShortcut.ADV_MACHINE.getItem();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return IC2ItemsShortcut.ADV_MACHINE.getMetadata();
	}

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
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
						item.copy());

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking())
			return false;
		else {
			if (!world.isRemote) {
				TileEntityWirelessPowerSynchronizer tileWirelessSync = (TileEntityWirelessPowerSynchronizer) world
						.getTileEntity(pos);
				if (tileWirelessSync != null) {
					player.openGui(LevelStorage.instance, 53, world, pos.getX(), pos.getY(), pos.getZ());
				}
			}

			return true;
		}
		// return false;
	}
	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public Icon getIcon(int side, int par2) {
	 * EnumFacing orientation = EnumFacing.VALID_DIRECTIONS[side]; if
	 * (orientation == EnumFacing.DOWN) return this.down; if (orientation ==
	 * EnumFacing.UP) return this.up; if (orientation == EnumFacing.NORTH ||
	 * orientation == EnumFacing.WEST || orientation == EnumFacing.SOUTH ||
	 * orientation == EnumFacing.EAST) return this.side; return null; }
	 * 
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public void registerIcons(IconRegister
	 * iconRegister) { this.side = iconRegister
	 * .registerIcon(ClientProxy.WIRELESS_POWER_SYNC_TEXTURE + "Side"); this.up
	 * = iconRegister .registerIcon(ClientProxy.WIRELESS_POWER_SYNC_TEXTURE +
	 * "Up"); this.down = iconRegister
	 * .registerIcon(ClientProxy.WIRELESS_POWER_SYNC_TEXTURE + "Down"); }
	 */

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWirelessPowerSynchronizer();
	}

}

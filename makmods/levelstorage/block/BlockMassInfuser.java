package makmods.levelstorage.block;

import ic2.api.item.Items;

import java.util.Random;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;

public class BlockMassInfuser extends BlockContainer {

	public static final String UNLOCALIZED_NAME = "blockMassInfuser";
	public static final String NAME = "Mass Infuser";

	public BlockMassInfuser(int id) {
		super(id, Material.iron);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
		this.setBlockBounds(0F, 0F, 0F, 1F, 0.375F, 1F);
		TileEntityWirelessConductor.getConfig();
	}

	public static void addCraftingRecipe() {
	}

	private Icon down;
	private Icon up;
	private Icon side;

	public ItemStack advMachine = Items.getItem("advancedMachine");

	// You don't want the normal render type, or it wont render properly.
	@Override
	public int getRenderType() {
		return -1;
	}

	// It's not an opaque cube, so you need this.
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	// It's not a normal block, so you need this too.
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return this.advMachine.itemID;
	}

	@Override
	public int damageDropped(int par1) {
		return this.advMachine.getItemDamage();
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

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (player.isSneaking())
			return false;
		else {
			if (!world.isRemote) {
				TileEntityMassInfuser tile = (TileEntityMassInfuser) world
						.getBlockTileEntity(x, y, z);
				if (tile != null) {
					player.openGui(LevelStorage.instance, 70, world, x, y, z);
				}
			}

			return true;
		}
		// return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityMassInfuser();
	}

}
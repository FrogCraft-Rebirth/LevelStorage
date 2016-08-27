package makmods.levelstorage.block;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.ItemFrequencyCard;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWirelessConductor extends BlockContainer implements IHasRecipe {

	public BlockWirelessConductor(int id) {
		super(Material.IRON);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(SoundType.METAL);
		this.setHardness(3.0F);
		TileEntityWirelessConductor.getConfig();
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return new AxisAlignedBB(0F, 0F, 0F, 1F, 0.375F, 1F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return new AxisAlignedBB(0F, 0F, 0F, 1F, 0.375F, 1F).offset(pos);
	}

	@Override
	public void addCraftingRecipe() {
		ItemStack frequencyTr = IC2Items.getItem("frequencyTransmitter");
		ItemStack transformerHv = IC2Items.getItem("hvTransformer");
		ItemStack advCircuit = IC2Items.getItem("advancedCircuit");
		ItemStack advMachine = IC2Items.getItem("advancedMachine");
		ItemStack enderPearl = new ItemStack(Items.ENDER_PEARL);
		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.blockWlessConductor), "tmt", "cec", "chc",
		        Character.valueOf('t'), frequencyTr, Character.valueOf('e'),
		        enderPearl, Character.valueOf('c'), advCircuit, Character
		                .valueOf('h'), transformerHv, Character.valueOf('m'),
		        advMachine);

	}
/*
	private Icon down;
	private Icon up;
	private Icon side;*/

	public ItemStack advMachine = IC2Items.getItem("advancedMachine");

	// You don't want the normal render type, or it wont render properly.
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	// It's not an opaque cube, so you need this.
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// It's not a normal block, so you need this too.
	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.advMachine.getItem();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.advMachine.getItemDamage();
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

				EntityItem entityItem = new EntityItem(world, pos.getX() + rx, 
					pos.getY() + ry, pos.getZ() + rz, item.copy());

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, 
			EntityPlayer player, EnumHand hand, ItemStack heldItem, 
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack stack = player.inventory.getCurrentItem();
			boolean isEmptyCard = false;
			if (stack != null) {
				NBTHelper.checkNBT(stack);
				if (stack.getItem() == LSBlockItemList.itemFreqCard) {
					isEmptyCard = !ItemFrequencyCard.hasCardData(stack);
				}
			}
			if (isEmptyCard) {
				LevelStorage.proxy.messagePlayer(player, "Card data set",
				        new Object[0]);
				return false;
			}
		}
		if (player.isSneaking())
			return false;
		else {
			if (!world.isRemote) {
				TileEntityWirelessConductor tileWirelessConductor = (TileEntityWirelessConductor) world
				        .getTileEntity(pos);
				if (tileWirelessConductor != null) {
					player.openGui(LevelStorage.instance, 52, world, pos.getX(), pos.getY(), pos.getZ());
				}
			}

			return true;
		}
		// return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWirelessConductor();
	}

}

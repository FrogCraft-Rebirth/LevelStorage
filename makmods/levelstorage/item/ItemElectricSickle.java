package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemElectricSickle extends ItemTool implements IElectricItem, IHasRecipe {

	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_BLOCK = 50;
	public static final int RADIUS = 10;
	public static final int RADIUS_LEAVES = 6;

	public ItemElectricSickle(int id) {
		super(ToolMaterial.IRON, ImmutableSet.of(Blocks.LEAVES, Blocks.LEAVES2, Blocks.TALLGRASS));

		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}
/*
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		if (par2Block != null) {
			if (par2Block.isLeaves(null, 0, 0, 0)
			        || par2Block instanceof IPlantable) {
				return EnumToolMaterial.IRON.getEfficiencyOnProperMaterial();
			}
		}
		return 1.0F;
	}*/

	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!world.isRemote) {
			if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK)) {
				ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, entityLiving);
				if (entityLiving instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entityLiving;
					Block blockBroken = state.getBlock();
					if (blockBroken != null) {
						if (blockBroken instanceof IPlantable) {
							// System.out
							// .println("blockBroken instanceof Blocktallgrass");
							ArrayList<BlockLocation> blocksToBreak = new ArrayList<BlockLocation>();
							for (int xCurr = -(RADIUS / 2); xCurr < (RADIUS / 2); xCurr++) {
								for (int zCurr = -(RADIUS / 2); zCurr < (RADIUS / 2); zCurr++) {
									// System.out.println("adding!");
									blocksToBreak.add(new BlockLocation(player.worldObj.provider.getDimension(), pos.add(xCurr, 0, zCurr)));
								}
							}
							for (BlockLocation curr : blocksToBreak) {
								// System.out.println("breakingblock");
								Block currBlock = world.getBlockState(curr.toBlockPos()).getBlock();
								if (currBlock != null) {
									// System.out.println(currBlock.getClass()
									// .getName());
									if (currBlock instanceof BlockTallGrass
									        || currBlock instanceof IPlantable) {
										// System.out.println("breakingblock");
										if (currBlock.removeBlockByPlayer(
										                par2World,
										                (EntityPlayer) par7EntityLivingBase,
										                curr.getX(),
										                curr.getY(),
										                curr.getZ())) {
											if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK))
												ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, player);
											else {
												break;
											}
											currBlock.dropBlockAsItem(
											        par2World, curr.getX(),
											        curr.getY(), curr.getZ(),
											        par2World.getBlockMetadata(
											                curr.getX(),
											                curr.getY(),
											                curr.getZ()), 2);
										}
									}
								}
							}
						}

						if (blockBroken.isLeaves(par2World, x, y, z)) {
							ArrayList<BlockLocation> blocksToBreak = new ArrayList<BlockLocation>();
							for (int yCurr = -(RADIUS_LEAVES / 2); yCurr < (RADIUS_LEAVES / 2); yCurr++) {
								for (int xCurr = -(RADIUS_LEAVES / 2); xCurr < (RADIUS_LEAVES / 2); xCurr++) {
									for (int zCurr = -(RADIUS_LEAVES / 2); zCurr < (RADIUS_LEAVES / 2); zCurr++) {
										// System.out.println("adding!");
										blocksToBreak.add(new BlockLocation(
										                player.worldObj.provider.dimensionId,
										                x + xCurr, y + yCurr, z
										                        + zCurr));
									}
								}
							}
							for (BlockLocation curr : blocksToBreak) {
								Block currBlock = world.getBlockState(curr.toBlockPos()).getBlock();
								if (currBlock != null) {
									if (currBlock.isLeaves(state, world, pos) {
										if (currBlock.removeBlockByPlayer(
										                world,
										                (EntityPlayer) entityLiving,
										                curr.getX(),
										                curr.getY(),
										                curr.getZ())) {
											if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK))
												ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, player);
											else {
												break;
											}
											currBlock.dropBlockAsItem(world, curr.toBlockPos(), world.getBlockState(curr.toBlockPos()), 2);
										}
									}
								}
							}
						}

					}
				}
			}
		}
		return true;
	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return 100;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ELECTRIC_SICKLE_TEXTURE);
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        'i', IC2Items.REFINED_IRON, 
				'e', IC2Items.BASIC_CIRCUIT, 
				'r', IC2Items.RE_BATTERY);
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        'i', IC2Items.REFINED_IRON, 
		        'e', IC2Items.BASIC_CIRCUIT, 
				'r', IC2Items.RE_BATTERY_CHARGED);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
		        true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
}

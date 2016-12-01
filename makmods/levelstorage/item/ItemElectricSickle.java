package makmods.levelstorage.item;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class ItemElectricSickle extends ItemTool implements IElectricItem, IHasRecipe {

	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_BLOCK = 50; //TODO: Configurable
	public static final int RADIUS = 5; // Used to be 10 - but the actual code divide this by 2, so let it be 5 for clean code
	public static final int RADIUS_LEAVES = 3; //Same as above, used ot be 6

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
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!world.isRemote) {
			if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK)) {
				ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, entityLiving);
				if (entityLiving instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entityLiving;
					Block blockBroken = state.getBlock();
					if (blockBroken != null && blockBroken instanceof IPlantable) {
						Iterable<BlockPos> blocksToBreak = BlockPos.getAllInBox(pos.add(-RADIUS, 0, RADIUS), pos.add(RADIUS, 0, RADIUS));
						for (BlockPos curr : blocksToBreak) {
							// System.out.println("breakingblock");
							IBlockState currBlock = world.getBlockState(curr);
							if (currBlock != null) {
								// System.out.println(currBlock.getClass().getName());
								if (currBlock.getBlock() instanceof BlockTallGrass || currBlock.getBlock() instanceof IPlantable) {
									// System.out.println("breakingblock");
									if (currBlock.getBlock().removedByPlayer(state, world, curr, player, true)) {
										if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK)) {
											ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, player);
											currBlock.getBlock().dropBlockAsItem(world, curr, currBlock, 2);
										} else {
											break;
										}
									}
								}
							}
						}
					}

					if (blockBroken.isLeaves(state, world, pos)) {
						Iterable<BlockPos> blocksToBreak = BlockPos.getAllInBox(pos.add(-RADIUS_LEAVES, -RADIUS_LEAVES, -RADIUS_LEAVES), pos.add(RADIUS_LEAVES, RADIUS_LEAVES, RADIUS_LEAVES));
						for (BlockPos curr : blocksToBreak) {
							IBlockState currBlock = world.getBlockState(curr);
							if (currBlock.getBlock().isLeaves(state, world, pos)) {
								if (currBlock.getBlock().removedByPlayer(state, world, curr, player, true)) {
									if (ElectricItem.manager.canUse(stack, ENERGY_PER_BLOCK)) {
										ElectricItem.manager.use(stack, ENERGY_PER_BLOCK, player);
										currBlock.getBlock().dropBlockAsItem(world, curr, currBlock, 2);
									} else {
										break;
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
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        'i', IC2ItemsShortcut.REFINED_IRON, 
				'e', IC2ItemsShortcut.BASIC_CIRCUIT, 
				'r', IC2ItemsShortcut.RE_BATTERY);
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        'i', IC2ItemsShortcut.REFINED_IRON, 
		        'e', IC2ItemsShortcut.BASIC_CIRCUIT, 
				'r', IC2ItemsShortcut.RE_BATTERY_CHARGED);
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

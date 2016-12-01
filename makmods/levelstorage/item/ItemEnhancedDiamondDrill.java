package makmods.levelstorage.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import ic2.api.util.Keys;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.DrillEnhancementRecipe;
import makmods.levelstorage.logic.util.AdvBlockFinder;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.logic.util.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ItemEnhancedDiamondDrill extends ItemPickaxe implements
		IElectricItem, IHasRecipe {

	public static final float SPEED; //Defined in a static initializer
	public static final int TIER = 2;
	public static final int STORAGE = 100000;
	public static final int ENERGY_PER_USE = 200;
	public static final String MODE_NBT = "mode";

	public static final Integer COOLDOWN = 20;
/*
	public Icon iconPass1;
	public Icon iconPass2;*/

	public static enum EnhancementUtility {
		FORTUNE_1(ImmutableMap.of(Enchantments.FORTUNE, 1)),
		FORTUNE_2(ImmutableMap.of(Enchantments.FORTUNE, 2)),
		FORTUNE_3(ImmutableMap.of(Enchantments.FORTUNE, 3)),
		SILKTOUCH(ImmutableMap.of(Enchantments.SILK_TOUCH, 1)),
		UNKNOWN;

		private final Map<Enchantment, Integer> enchantment;

		EnhancementUtility() {
			this.enchantment = null;
		}

		EnhancementUtility(Map<Enchantment, Integer> enchantment) {
			this.enchantment = enchantment;
		}

		public ItemStack createDrill() {
			ItemStack drillStack = new ItemStack(LSBlockItemList.itemEnhDiamondDrill);
			if (this.enchantment != null) {
				EnchantmentHelper.setEnchantments(this.enchantment, drillStack);
			}
			return drillStack;
		}
	}

	public ItemEnhancedDiamondDrill(int id) {
		super(ToolMaterial.DIAMOND); //EnumToolMaterial.EMERALD -> ToolMaterial.DIAMOND, legacy typo
		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
		this.damageVsEntity = 2.0f;
		this.efficiencyOnProperMaterial = SPEED;
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		return ImmutableSet.of("shovel", "pickaxe");
	}

	static {
		Property p = LevelStorage.configuration
				.get(Configuration.CATEGORY_GENERAL,
						"enhancedDiamondDrillSpeed", 32);
		p.setComment("Speed of enhanced diamond drill (diamond drill = 16, default = 32)");
		SPEED = p.getInt(32);
	}

	public static enum Mode {
		TUNNEL("3x3x1 (width x height x depth)"), NORMAL("normal (1x)");

		public String name;

		Mode(String par1Str) {
			this.name = par1Str;
		}
	}

	private static void invertMode(ItemStack stack, EntityPlayer player) {
		try {
			if (!NBTHelper.verifyKey(stack, MODE_NBT)) {
				NBTHelper.setInteger(stack, MODE_NBT, Mode.TUNNEL.ordinal());
				return;
			}
			Mode mode = Mode.values()[NBTHelper.getInteger(stack, MODE_NBT)];
			switch (mode) {
			case TUNNEL: {
				NBTHelper.setInteger(stack, MODE_NBT, Mode.NORMAL.ordinal());
				break;
			}
			case NORMAL: {
				NBTHelper.setInteger(stack, MODE_NBT, Mode.TUNNEL.ordinal());
				break;
			}
			}
			LevelStorage.proxy.messagePlayer(
					player,
					"Drill mode: "
							+ Mode.values()[NBTHelper.getInteger(stack,
									MODE_NBT)].name, new Object[0]);
		} catch (Exception e) {
			System.out.println(Reference.MOD_NAME
					+ ": something went wrong when tried to set drill's mode.");
			e.printStackTrace();
		}
	}

	/**
	 * Drill's onUpdate method. Used only for cooldown
	 */
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIn, boolean isSelected) {
		if (!world.isRemote) {
			if (!NBTHelper.verifyKey(stack, MODE_NBT))
				NBTHelper.setInteger(stack, MODE_NBT,
						Mode.NORMAL.ordinal());
			Cooldownable.onUpdate(stack, COOLDOWN);
		}
	}

	/**
	 * Drill's onItemRightClick method. Used only for mode change.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (Keys.instance.isModeSwitchKeyDown(player)) {
				if (!Cooldownable.use(itemStack, COOLDOWN))
					return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
				// Unneeded really, but i'll keep it here
				NBTHelper.checkNBT(itemStack);
				invertMode(itemStack, player);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStack);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return false;
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemEnhDiamondDrill),
				"cdc", "did", "aea",
					'c', IC2ItemsShortcut.CARBON_PLATE,
					'e', IC2Items.getItem("advanced_re_battery"),
					'i', IC2Items.getItem("diamond_drill"),
					'a', IC2ItemsShortcut.ADV_CIRCUIT,
					'd', "gemDiamond");
		CraftingManager.getInstance().getRecipeList().add(new DrillEnhancementRecipe());
	}

	public static ArrayList<Block> mineableBlocks = new ArrayList<>();

	static {
		mineableBlocks.add(Blocks.COBBLESTONE);
		mineableBlocks.add(Blocks.STONE_SLAB);
		mineableBlocks.add(Blocks.STONE_SLAB2);
		mineableBlocks.add(Blocks.STONE_STAIRS);
		mineableBlocks.add(Blocks.STONE);
		mineableBlocks.add(Blocks.SANDSTONE);
		mineableBlocks.add(Blocks.SANDSTONE_STAIRS);
		mineableBlocks.add(Blocks.MOSSY_COBBLESTONE);
		mineableBlocks.add(Blocks.IRON_ORE);
		mineableBlocks.add(Blocks.IRON_BLOCK);
		mineableBlocks.add(Blocks.COAL_ORE);
		mineableBlocks.add(Blocks.COAL_BLOCK);
		mineableBlocks.add(Blocks.GOLD_BLOCK);
		mineableBlocks.add(Blocks.GOLD_ORE);
		mineableBlocks.add(Blocks.DIAMOND_ORE);
		mineableBlocks.add(Blocks.DIAMOND_BLOCK);
		mineableBlocks.add(Blocks.ICE);
		mineableBlocks.add(Blocks.PACKED_ICE);
		mineableBlocks.add(Blocks.NETHERRACK);
		mineableBlocks.add(Blocks.LAPIS_ORE);
		mineableBlocks.add(Blocks.LAPIS_BLOCK);
		mineableBlocks.add(Blocks.REDSTONE_ORE);
		mineableBlocks.add(Blocks.LIT_REDSTONE_ORE);
		mineableBlocks.add(Blocks.BRICK_BLOCK);
		mineableBlocks.add(Blocks.BRICK_STAIRS);
		mineableBlocks.add(Blocks.GLOWSTONE);
		mineableBlocks.add(Blocks.GRASS);
		mineableBlocks.add(Blocks.DIRT);
		mineableBlocks.add(Blocks.MYCELIUM);
		mineableBlocks.add(Blocks.SAND);
		mineableBlocks.add(Blocks.GRAVEL);
		mineableBlocks.add(Blocks.SNOW_LAYER);
		mineableBlocks.add(Blocks.SNOW);
		mineableBlocks.add(Blocks.CLAY);
		mineableBlocks.add(Blocks.FARMLAND); //Used to be called tilled field
		mineableBlocks.add(Blocks.STONEBRICK);
		mineableBlocks.add(Blocks.STONE_BRICK_STAIRS);
		mineableBlocks.add(Blocks.NETHER_BRICK);
		mineableBlocks.add(Blocks.NETHER_BRICK_STAIRS);
		mineableBlocks.add(Blocks.SOUL_SAND); //Used to be called slow sand
		mineableBlocks.add(Blocks.ANVIL);
		mineableBlocks.add(Blocks.QUARTZ_ORE);
	}

	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.RARE;
	}

	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean adv) {
		tooltip.add(I18n.format("tooltip.drillEnhancement"));
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		if (enchantments.isEmpty()) {
			tooltip.add("\247c" + I18n.format("tooltip.drillEnhancement.none"));
		} else { //Assuming that the size of map is 1. Even if it's not 1, this would still work.
			enchantments.forEach((ench, level) -> tooltip.add(ench.getTranslatedName(level)));
		}
	}
/*
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public Icon getIconFromDamageForRenderPass(int par1, int par2) {
		switch (par2) {
		case 0:
			return iconPass1;
		case 1:
			return iconPass2;
		}
		return null;
	}*/

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
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
		return 2000;
	}

	private static final List<Map<Enchantment, Integer>> ENCH_COMBO = ImmutableList.of(
			ImmutableMap.of(Enchantments.FORTUNE, 1),
			ImmutableMap.of(Enchantments.FORTUNE, 2),
			ImmutableMap.of(Enchantments.FORTUNE, 3),
			ImmutableMap.of(Enchantments.SILK_TOUCH, 1)
	);

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));

		for (Map<Enchantment, Integer> enchantment : ENCH_COMBO) {
			ItemStack charged = new ItemStack(this, 1);
			ElectricItem.manager.charge(charged, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
			EnchantmentHelper.setEnchantments(enchantment, charged);
			list.add(charged);

			ItemStack discharged = new ItemStack(this, 1, this.getMaxDamage());
			ElectricItem.manager.charge(discharged, 0, 1, true, false);
			EnchantmentHelper.setEnchantments(enchantment, discharged);
			list.add(discharged);
		}
	}

	public int getItemEnchantability() {
		return 0;
	}

	public static ArrayList<Block> blocksOtherThanOres = new ArrayList<>();

	static {
		blocksOtherThanOres.add(Blocks.GRAVEL);
		blocksOtherThanOres.add(Blocks.REDSTONE_ORE);
		blocksOtherThanOres.add(Blocks.LIT_REDSTONE_ORE);
		blocksOtherThanOres.add(Blocks.GLOWSTONE);
		blocksOtherThanOres.add(Blocks.CLAY);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (world.isRemote) {
			int fortune = 0;
			boolean silktouch;
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			if (enchantments.containsKey(Enchantments.FORTUNE)) {
				fortune = enchantments.get(Enchantments.FORTUNE);
			}
			silktouch = enchantments.containsKey(Enchantments.SILK_TOUCH);

			if (ElectricItem.manager.canUse(stack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(stack, ENERGY_PER_USE, entityLiving);
			}
			Block bl = state.getBlock();
			/*
			 * if (OreDictHelper.getOreName(new ItemStack(bl)).startsWith("ore")
			 * || blocksOtherThanOres.contains(bl.blockID)) { OreFinder finder =
			 * new OreFinder(); finder.aimBlockId = bl.blockID;
			 * finder.aimBlockMeta = par2World.getBlockMetadata(par4, par5,
			 * par6); finder.world = par2World; finder.calibrate(par4, par5,
			 * par6); for (BlockLocation oreCh : finder.foundOre) { int blockId
			 * = par2World.getBlockId(oreCh.getX(), oreCh.getY(), oreCh.getZ());
			 * 
			 * int blockMeta = par2World.getBlockMetadata(oreCh.getX(),
			 * oreCh.getY(), oreCh.getZ()); Block b = Block.blocksList[blockId];
			 * if (b != null) { if (par7EntityLivingBase instanceof
			 * EntityPlayer) { if (b.getBlockHardness(par2World, oreCh.getX(),
			 * oreCh.getY(), oreCh.getZ()) != -1.0F) { if
			 * (b.removeBlockByPlayer(par2World, (EntityPlayer)
			 * par7EntityLivingBase, oreCh.getX(), oreCh.getY(), oreCh.getZ()))
			 * {
			 * 
			 * if (!silktouch) { b.dropBlockAsItem(par2World, oreCh.getX(),
			 * oreCh.getY(), oreCh.getZ(), finder.aimBlockMeta, fortune); } else
			 * { if (b.canSilkHarvest( par2World, (EntityPlayer)
			 * par7EntityLivingBase, oreCh.getX(), oreCh.getY(), oreCh.getZ(),
			 * finder.aimBlockMeta)) { ItemStack itemstack = new ItemStack( b,
			 * 1, finder.aimBlockMeta); if (itemstack != null) {
			 * this.dropBlockAsItem_do( par2World, oreCh.getX(), oreCh.getY(),
			 * oreCh.getZ(), itemstack); } } else { b.dropBlockAsItem(par2World,
			 * oreCh.getX(), oreCh.getY(), oreCh.getZ(), finder.aimBlockMeta,
			 * fortune); } } } if (ElectricItem.manager.canUse(par1ItemStack,
			 * ENERGY_PER_USE)) { ElectricItem.manager.use(par1ItemStack,
			 * ENERGY_PER_USE, par7EntityLivingBase); } else { break; } } } } }
			 * }
			 */
			String name = OreDictHelper.getOreName(new ItemStack(bl));
			int metadata = state.getBlock().getMetaFromState(state);
			if (name.startsWith("ore")) {
				AdvBlockFinder finder = new AdvBlockFinder(world, pos, name);
				for (BlockLocation oreCh : finder.getBlocksFound()) {
					IBlockState blockFound = world.getBlockState(oreCh.toBlockPos());
					Block b = blockFound.getBlock();
					if (entityLiving instanceof EntityPlayer) {
						if (b.getBlockHardness(blockFound, world, oreCh.toBlockPos()) != -1.0F) {
							if (b.canEntityDestroy(blockFound, world, oreCh.toBlockPos(), entityLiving)) {
								if (!silktouch) {
										b.dropBlockAsItem(world, oreCh.toBlockPos(), blockFound, fortune);
								} else {
									if (b.canSilkHarvest(world, oreCh.toBlockPos(), blockFound, (EntityPlayer) entityLiving)) {
										ItemStack itemstack = new ItemStack(b,1, metadata);
										this.dropBlockAsItem_do(world, oreCh.getX(), oreCh.getY(), oreCh.getZ(), itemstack);
									} else {
										b.dropBlockAsItem(world, oreCh.toBlockPos(), blockFound, fortune);
									}
								}
							}
							if (ElectricItem.manager.canUse(stack, ENERGY_PER_USE)) {
								ElectricItem.manager.use(stack, ENERGY_PER_USE, entityLiving);
							} else {
								break;
							}
						}
					}
				}
			} else {
				if (NBTHelper.getInteger(stack, MODE_NBT) == Mode.TUNNEL.ordinal()) {
					if (entityLiving instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entityLiving;
						if (!player.isSneaking()) {
							RayTraceResult rayTrace = this.rayTrace(world, player, true);
							if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
								EnumFacing hitFrom = rayTrace.sideHit;
								BlockLocation currBlock = new BlockLocation(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ());
								BlockLocation[] removeBlocks = new BlockLocation[12];
								switch (hitFrom) {
									case DOWN: {
										removeBlocks[0] = currBlock.move(EnumFacing.NORTH, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.WEST, 1);
										removeBlocks[2] = currBlock.move(EnumFacing.SOUTH, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);

										removeBlocks[4] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.EAST, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.WEST, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[6] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.WEST, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.WEST, 1).move(EnumFacing.SOUTH, 1);
										removeBlocks[8] = currBlock.move(EnumFacing.SOUTH, 1).move(EnumFacing.EAST, 1);
										break;
									}
									case UP: {
										removeBlocks[0] = currBlock.move(EnumFacing.NORTH, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.WEST, 1);
										removeBlocks[2] = currBlock.move(EnumFacing.SOUTH, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);

										removeBlocks[4] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.EAST, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.WEST, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[6] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.WEST, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.WEST, 1).move(EnumFacing.SOUTH, 1);
										removeBlocks[8] = currBlock.move(EnumFacing.SOUTH, 1).move(EnumFacing.EAST, 1);
										break;
									}
									case NORTH: {
										// Up & down
										removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
										// West & east
										removeBlocks[2] = currBlock.move(EnumFacing.WEST, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);
										// Up-west & Down-west
										removeBlocks[4] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.WEST, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.EAST, 1);

										removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.WEST, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.EAST, 1);

										removeBlocks[8] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.EAST, 1);
										removeBlocks[9] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.WEST, 1);

										break;
										// South up & north down
									}
									case WEST: {
										removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
										// West & east
										removeBlocks[2] = currBlock.move(EnumFacing.NORTH, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.SOUTH, 1);

										removeBlocks[4] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.SOUTH, 1);

										removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.SOUTH, 1);
										removeBlocks[10] = currBlock.move(EnumFacing.SOUTH, 1).move(EnumFacing.UP, 1);
										removeBlocks[11] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.DOWN, 1);
										break;
									}
									case EAST: {
										removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
										// West & east
										removeBlocks[2] = currBlock.move(EnumFacing.NORTH, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.SOUTH, 1);

										removeBlocks[4] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.SOUTH, 1);

										removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.NORTH, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.SOUTH, 1);
										removeBlocks[10] = currBlock.move(EnumFacing.SOUTH, 1).move(EnumFacing.UP, 1);
										removeBlocks[11] = currBlock.move(EnumFacing.NORTH, 1).move(EnumFacing.DOWN, 1);
										break;
									}
									case SOUTH: {
										removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
										removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
										// West & east
										removeBlocks[2] = currBlock.move(EnumFacing.WEST, 1);
										removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);
										// Up-west & Down-west
										removeBlocks[4] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.WEST, 1);
										removeBlocks[5] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.EAST, 1);

										removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.WEST, 1);
										removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.EAST, 1);

										removeBlocks[8] = currBlock.move(EnumFacing.UP, 1).move(EnumFacing.EAST, 1);
										removeBlocks[9] = currBlock.move(EnumFacing.DOWN, 1).move(EnumFacing.WEST, 1);
										break;
									}
									default:
										break;
									}
									// Stuff destroying part.. messy code, but it works.
									for (BlockLocation blockLoc : removeBlocks) {
										if (blockLoc != null) {
											IBlockState bs = world.getBlockState(blockLoc.toBlockPos());
											int aimBlockMeta = bs.getBlock().getMetaFromState(bs);
											if (bs.getBlockHardness(world, blockLoc.toBlockPos()) != -1.0F) {
												if (bs.getBlock().canEntityDestroy(bs, world, blockLoc.toBlockPos(), entityLiving)) {
													if (!silktouch) {
														bs.getBlock().dropBlockAsItem(world, blockLoc.toBlockPos(), bs, fortune);
													} else {
														if (bs.getBlock().canSilkHarvest(world, blockLoc.toBlockPos(), bs, (EntityPlayer) entityLiving)) {
															ItemStack itemstack = new ItemStack(bs.getBlock(), 1, aimBlockMeta);
															this.dropBlockAsItem_do(world, blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), itemstack);
														} else {
															bs.getBlock().dropBlockAsItem(world, blockLoc.toBlockPos(), bs, fortune);
														}
													}
												}
												if (ElectricItem.manager.canUse(stack, ENERGY_PER_USE)) {
														ElectricItem.manager.use(stack, ENERGY_PER_USE, entityLiving);
												} else {
													break;
												}
											}
										}
									}
										// End of messy part
								}
						}
					}
				}
			}
		}
		return true;
	}

	// From Block
	public void dropBlockAsItem_do(World par1World, int par2, int par3,
			int par4, ItemStack par5ItemStack) {
		if (!par1World.isRemote
				&& par1World.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.7F;
			double d0 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d1 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d2 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(par1World, (double) par2
					+ d0, (double) par3 + d1, (double) par4 + d2, par5ItemStack);
			entityitem.setPickupDelay(10);
			par1World.spawnEntityInWorld(entityitem);
		}
	}
/*
	//md -> metadata, getStrVsBlock -> getStrengthVersusBlock
	public float getStrVsBlock(ItemStack itemstack, Block block, int md) {
		if (!ElectricItem.manager.canUse(itemstack, ENERGY_PER_USE)) {
			return 1.0F;
		}
		if (ForgeHooks.isToolEffective(itemstack, block, 0)) {
			return this.efficiencyOnProperMaterial;
		}
		if (mineableBlocks.contains(block)) {
			return this.efficiencyOnProperMaterial;
		}
		if (canHarvestBlock(block)) {
			return this.efficiencyOnProperMaterial;
		}

		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.iconPass1 = par1IconRegister
				.registerIcon(ClientProxy.ITEM_ENHANCED_DIAMOND_DRILL_PASS_ONE);
		this.iconPass2 = par1IconRegister
				.registerIcon(ClientProxy.ITEM_ENHANCED_DIAMOND_DRILL_PASS_TWO);
	}*/

}

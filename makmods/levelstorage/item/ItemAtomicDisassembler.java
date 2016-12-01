package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.IChargeable;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.packet.PacketParticles;
import makmods.levelstorage.network.packet.PacketParticles.ParticleInternal;
import makmods.levelstorage.network.packet.PacketTypeHandler;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.LSKeyboard;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAtomicDisassembler extends Item implements IElectricItem,
		IChargeable, IHasRecipe {

	public static final int TIER = 3;
	public static final int STORAGE = 2000000;
	public static final int COOLDOWN_USE = 10;
	public static final int ENERGY_USE_BASE = 150;

	public static int MAX_LENGTH = 7;

	public ItemAtomicDisassembler(int id) {
		super();
		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		MAX_LENGTH = LevelStorage.configuration.get(LevelStorage.BALANCE_CATEGORY, "atomicDisassemblerMaxLength", 7,
						"Maximum tunnel length for Atomic Disassemblers (power of 2, default = 7 = 128)")
				.getInt(7);
		this.setMaxStackSize(1);
	}

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
		return 10000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.DESTRUCTOR_TEXTURE);
	}*/

	public static boolean DEAL_DAMAGE;
	public static boolean DEAL_DAMAGE_TO_OTHERS;

	static {
		DEAL_DAMAGE = LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"atomicDisassemblersEnableDamage", true).getBoolean(true);
	}

	public boolean isNumberNegative(int number) {
		return number < 0;
	}

	public void changeCharge(ItemStack itemStack, World world,
			EntityPlayer player) {
		if (player.inventory.getCurrentItem() != itemStack)
			return;
		int initialCharge = getChargeFor(itemStack);
		if (player.isSneaking()) {
			if (Cooldownable.use(itemStack, COOLDOWN_USE)) {
				setChargeFor(itemStack, getChargeFor(itemStack) - 1);
			}
		} else {
			if (Cooldownable.use(itemStack, COOLDOWN_USE)) {
				setChargeFor(itemStack, getChargeFor(itemStack) + 1);
			}
		}
		if (initialCharge != getChargeFor(itemStack))
			LevelStorage.proxy
					.messagePlayer(
							player,
							"Tunnel length: "
									+ (int) Math
											.pow(2, getChargeFor(itemStack)),
							new Object[0]);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// TODO: check this, if everything goes the way it should,
		// leave it as is, if not then fallback to !world.isRemote
		if (LevelStorage.isSimulating()) {
			int length = (int) Math.pow(2, getChargeFor(stack));
			int maxDamage = (int) (length / 4 * 1.5);
			int damage = player.worldObj.rand.nextInt(maxDamage + 1);
			if (DEAL_DAMAGE)
				player.attackEntityFrom(LSDamageSource.disassembled, damage);
			List<ParticleInternal> toSend = Lists.newArrayList();
			for (int curr = 0; curr < length; curr++) {
				BlockLocation loc = new BlockLocation(world.provider.getDimension(), pos).move(facing.getOpposite(), curr);
				mineThreeByThree(stack, facing, loc, world, player, toSend);
			}
			PacketParticles packet = new PacketParticles();
			packet.particles = toSend;
			//Packet250CustomPayload p = (Packet250CustomPayload) PacketTypeHandler.populatePacket(packet);
			PacketDispatcher.sendPacketToAllAround(packet, world.provider.getDimension(), pos, 128);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public static List<Block> bulkItemsToDelete = Lists.newArrayList();

	static {
		bulkItemsToDelete.add(Blocks.COBBLESTONE);
		bulkItemsToDelete.add(Blocks.DIRT);
		bulkItemsToDelete.add(Blocks.GRAVEL);
	}

	public void mineThreeByThree(ItemStack device, EnumFacing hitFrom,
			BlockLocation currBlock, World par2World, EntityPlayer player,
			List<ParticleInternal> particles) {
		BlockLocation[] removeBlocks = new BlockLocation[13];
		int fortune = 0;
		switch (hitFrom) {
		case DOWN: {
			removeBlocks[0] = currBlock.move(EnumFacing.NORTH, 1);
			removeBlocks[1] = currBlock.move(EnumFacing.WEST, 1);
			removeBlocks[2] = currBlock.move(EnumFacing.SOUTH, 1);
			removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);

			removeBlocks[4] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.EAST, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.WEST, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[6] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.WEST, 1).move(
					EnumFacing.SOUTH, 1);
			removeBlocks[8] = currBlock.move(EnumFacing.SOUTH, 1).move(
					EnumFacing.EAST, 1);
			break;
		}
		case UP: {
			removeBlocks[0] = currBlock.move(EnumFacing.NORTH, 1);
			removeBlocks[1] = currBlock.move(EnumFacing.WEST, 1);
			removeBlocks[2] = currBlock.move(EnumFacing.SOUTH, 1);
			removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);

			removeBlocks[4] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.EAST, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.WEST, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[6] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.WEST, 1).move(
					EnumFacing.SOUTH, 1);
			removeBlocks[8] = currBlock.move(EnumFacing.SOUTH, 1).move(
					EnumFacing.EAST, 1);
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
			removeBlocks[4] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.EAST, 1);

			removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.EAST, 1);

			removeBlocks[8] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.EAST, 1);
			removeBlocks[9] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.WEST, 1);

			break;
			// South up & north down
		}
		case WEST: {
			removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
			removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
			// West & east
			removeBlocks[2] = currBlock.move(EnumFacing.NORTH, 1);
			removeBlocks[3] = currBlock.move(EnumFacing.SOUTH, 1);

			removeBlocks[4] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.SOUTH, 1);

			removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.SOUTH, 1);
			removeBlocks[10] = currBlock.move(EnumFacing.SOUTH, 1).move(
					EnumFacing.UP, 1);
			removeBlocks[11] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.DOWN, 1);
			break;
		}
		case EAST: {
			removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
			removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
			// West & east
			removeBlocks[2] = currBlock.move(EnumFacing.NORTH, 1);
			removeBlocks[3] = currBlock.move(EnumFacing.SOUTH, 1);

			removeBlocks[4] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.SOUTH, 1);

			removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.NORTH, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.SOUTH, 1);
			removeBlocks[10] = currBlock.move(EnumFacing.SOUTH, 1).move(
					EnumFacing.UP, 1);
			removeBlocks[11] = currBlock.move(EnumFacing.NORTH, 1).move(
					EnumFacing.DOWN, 1);
			break;
		}
		case SOUTH: {
			removeBlocks[0] = currBlock.move(EnumFacing.UP, 1);
			removeBlocks[1] = currBlock.move(EnumFacing.DOWN, 1);
			// West & east
			removeBlocks[2] = currBlock.move(EnumFacing.WEST, 1);
			removeBlocks[3] = currBlock.move(EnumFacing.EAST, 1);
			// Up-west & Down-west
			removeBlocks[4] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[5] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.EAST, 1);

			removeBlocks[6] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.WEST, 1);
			removeBlocks[7] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.EAST, 1);

			removeBlocks[8] = currBlock.move(EnumFacing.UP, 1).move(
					EnumFacing.EAST, 1);
			removeBlocks[9] = currBlock.move(EnumFacing.DOWN, 1).move(
					EnumFacing.WEST, 1);
			break;
		}
		default:
			break;
		}
		removeBlocks[12] = currBlock.copy();
		if (!ElectricItem.manager.canUse(device, ENERGY_USE_BASE))
			return;
		for (BlockLocation blockLoc : removeBlocks) {
			if (blockLoc != null) {
				Block b = par2World.getBlockState(blockLoc.toBlockPos()).getBlock();
				int aimBlockMeta = b.getMetaFromState(par2World.getBlockState(blockLoc.toBlockPos()));
				if (b != null) {
					if (b.getBlockHardness(par2World, blockLoc.getX(),
							blockLoc.getY(), blockLoc.getZ()) > 0.0F) {
						if (b.removeBlockByPlayer(par2World, player,
								blockLoc.getX(), blockLoc.getY(),
								blockLoc.getZ())) {
							List<ItemStack> drops = b.getBlockDropped(
									par2World, blockLoc.getX(),
									blockLoc.getY(), blockLoc.getZ(),
									aimBlockMeta, 0);
							for (ItemStack drop : drops) {
								ParticleInternal particle = new ParticleInternal();
								particle.name = "tilecrack_" + b + "_"
										+ aimBlockMeta;
								particle.x = blockLoc.getX();
								particle.y = blockLoc.getY();
								particle.z = blockLoc.getZ();
								particle.velX = 0.0f;
								particle.velY = 0.5f;
								particle.velZ = 0.0f;
								particles.add(particle);
								if (!bulkItemsToDelete.contains(drop)) {

									CommonHelper.dropBlockInWorld_exact(
											par2World, player.posX,
											player.posY + 1.6f, player.posZ,
											drop);
								}
								// if (Items.getItem("uraniumDrop").itemID ==
								// drop.itemID) {
								// par2World.createExplosion(null,
								// blockLoc.getX(), blockLoc.getY(),
								// blockLoc.getZ(), 6F, true);
								// }

							}
						}
					}
					if (ElectricItem.manager.canUse(device, ENERGY_USE_BASE)) {
						ElectricItem.manager.use(device, ENERGY_USE_BASE,
								player);
					} else {
						break;
					}
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			if (!(par3Entity instanceof EntityPlayerMP))
				return;
			EntityPlayerMP player = (EntityPlayerMP) par3Entity;
			if (!NBTHelper.verifyKey(par1ItemStack, IChargeable.CHARGE_NBT))
				NBTHelper.setInteger(par1ItemStack, IChargeable.CHARGE_NBT, 0);
			Cooldownable.onUpdate(par1ItemStack, COOLDOWN_USE);
			if (LSKeyboard.getInstance().isKeyDown(player,
					LSKeyboard.RANGE_KEY_NAME)) {
				changeCharge(par1ItemStack, par2World, player);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips, boolean adv) {
		String[] lines = I18n.format("tooltip.atomicDisassembler").split("\n");
		for (String line : lines) {
			tooltips.add("\247c" + line);
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	public void addCraftingRecipe() {
		// Recipes.advRecipes.addRecipe(new ItemStack(
		// LSBlockItemList.itemAtomicDisassembler), "cee", "ccd", "ccc",
		// Character.valueOf('c'), IC2Items.CARBON_PLATE, Character
		// .valueOf('e'), IC2Items.ENERGY_CRYSTAL, Character
		// .valueOf('d'), new ItemStack(
		// LSBlockItemList.itemEnhDiamondDrill));
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemAtomicDisassembler), "ccc", "lda", "ccc",
					'c', IC2ItemsShortcut.CARBON_PLATE, 
					'l', ic2.api.item.IC2Items.getItem("miningLaser"), 
					'd', new ItemStack(LSBlockItemList.itemEnhDiamondDrill), 
					'a', IC2ItemsShortcut.ADV_CIRCUIT);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}

	@Override
	public int getChargeFor(ItemStack stack) {
		return NBTHelper.getInteger(stack, IChargeable.CHARGE_NBT);
	}

	@Override
	public void setChargeFor(ItemStack stack, int charge) {
		if (charge > getMaxCharge()) {
			NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, getMaxCharge());
			return;
		} else if (charge < 0) {
			NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, 0);
			return;
		}
		NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, charge);
	}

	@Override
	// might be too much...
	public int getMaxCharge() {
		return MAX_LENGTH;
	}
}

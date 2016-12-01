package makmods.levelstorage.item;

import java.util.List;
import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.init.Config;
import makmods.levelstorage.init.Config.LSConfigCategory;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.logic.ExperienceRecipe;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.LSKeyboard;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemXPTome extends Item implements IHasRecipe {

	private int bookMaxStorage;
	public static final int COOLDOWN = 3;

	public static String STORED_XP_NBT = "storedXP";
	public static int xpPerInteraction = 100;
	public static int XP_PER_BOOKCRAFT = Config.getInt(LSConfigCategory.BALANCE, "xpPerBookEnchantment", 840, "Determines how much XP is consumed from XP tome when you try to enchant a book with it (default V while holding XP tome in your hand and having books in inventory");

	public ItemXPTome(int id) {
		super();
		this.bookMaxStorage = LevelStorage.itemLevelStorageBookSpace;
		this.setMaxDamage(512);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		// Book
		ItemStack stackDepleted = new ItemStack(
				LSBlockItemList.itemLevelStorageBook, 1, 0);
		stackDepleted.setTagCompound(new NBTTagCompound());
		ItemStack stackBook = new ItemStack(Items.BOOK);
		ItemStack stackGoldBlock = new ItemStack(Blocks.GOLD_BLOCK);
		ItemStack stackEnchTable = new ItemStack(Blocks.ENCHANTING_TABLE);
		GameRegistry.addShapelessRecipe(stackDepleted, stackBook,
				stackGoldBlock, stackEnchTable);
		if (LevelStorage.experienceRecipesOn) {
			CraftingManager.getInstance().getRecipeList()
					.add(new ExperienceRecipe());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (!Cooldownable.use(itemStack, COOLDOWN))
				return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
			if (player.isSneaking()) {
				if ((this.bookMaxStorage - getStoredXP(itemStack)) > player.experienceTotal) {
					increaseStoredXP(itemStack, player.experienceTotal);
					player.experienceTotal = 0;
					player.experienceLevel = 0;
					player.experience = 0;
					// par3EntityPlayer.setScore(0);
				}
			} else {
				xpPerInteraction = player.xpBarCap();
				if (getStoredXP(itemStack) > xpPerInteraction) {
					player.addExperience(xpPerInteraction);
					increaseStoredXP(itemStack, -xpPerInteraction);
					float f = 5 / 30.0F;
					world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, f * 0.75F, 1.0F);
				} else {
					if (getStoredXP(itemStack) != 0) {
						player.addExperience(getStoredXP(itemStack));
						itemStack.getTagCompound().setInteger(STORED_XP_NBT, 0);
						float f = 5 / 30.0F;
						world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, f * 0.75F, 1.0F);
					}
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}

	public static void increaseStoredXP(ItemStack stack, int amount) {
		NBTHelper.checkNBT(stack);
		NBTHelper.decreaseIntegerIgnoreZero(stack, STORED_XP_NBT, -1 * amount);
	}

	public static int getStoredXP(ItemStack stack) {
		NBTHelper.checkNBT(stack);
		return NBTHelper.getInteger(stack, STORED_XP_NBT);
	}

	public ItemStack generateRandomBook(Random rand) {
		Enchantment selected = Enchantment.REGISTRY.getRandomObject(rand);
		int level = selected.getMaxLevel() == 1 ? 1 : rand.nextInt(selected.getMaxLevel() - 1) + 1;
		ItemStack enchantedBook = CommonHelper.createEnchantedBook(selected, level);
		return enchantedBook;
	}

	public void attemptCreateEnchBook(ItemStack xpTome, World par2World,
			EntityPlayer player) {
		if (!(getStoredXP(xpTome) >= XP_PER_BOOKCRAFT))
			return;
		LogicSlot[] playerSlots = new LogicSlot[player.inventory.mainInventory.length];
		for (int i = 0; i < player.inventory.mainInventory.length; i++)
			playerSlots[i] = new LogicSlot(player.inventory, i);
		LogicSlot booksSlot = null;
		for (LogicSlot slot : playerSlots) {
			if (slot.get() != null)
				if (slot.get().getItem() instanceof ItemBook) {
					booksSlot = slot;
					break;
				}
		}
		if (booksSlot != null && booksSlot.get() != null) {
			if (booksSlot.canConsume(1)) {
				boolean shouldConsume = false;
				for (LogicSlot slot : playerSlots) {
					if (slot.get() == null && slot.add(generateRandomBook(par2World.rand), false)) {
						shouldConsume = true;
						break;
					}
				}
				if (shouldConsume) {
					increaseStoredXP(xpTome, -XP_PER_BOOKCRAFT);
					booksSlot.consume(1);
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			Cooldownable.onUpdate(par1ItemStack, COOLDOWN);
			this.setDamage(par1ItemStack, calculateDurability(par1ItemStack));
			if (par3Entity instanceof EntityPlayerMP) {
				EntityPlayer player = (EntityPlayer)par3Entity;
				if (player.inventory.getCurrentItem() != par1ItemStack)
					return;
				if (LSKeyboard.getInstance().isKeyDown(
						(EntityPlayer) par3Entity, LSKeyboard.RANGE_KEY_NAME)) {
					if (Cooldownable.use(par1ItemStack, COOLDOWN * 4)) {
						attemptCreateEnchBook(par1ItemStack, par2World,
								(EntityPlayer) par3Entity);
					}
				}
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips, boolean adv) {
		// Here we add our nice little tooltip
		super.addInformation(stack, player, tooltips, adv);
		NBTHelper.checkNBT(stack);
		tooltips.add(I18n.format("tooltip.xptome.stored")
				+ " " + String.valueOf(getStoredXP(stack)) + " "
				+ I18n.format("tooltip.xptome.xppoints"));
	}

	public static int calculateDurability(ItemStack stack) {
		float percent = ((getStoredXP(stack) * 100.0f) / LevelStorage.itemLevelStorageBookSpace) / 100;
		int durability = stack.getMaxDamage()
				- (int) (stack.getMaxDamage() * percent);
		if (durability == 0) {
			durability = 1;
		}
		return durability;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack stackFull = new ItemStack(item, 1, 1);
		stackFull.setTagCompound(new NBTTagCompound());
		stackFull.getTagCompound().setInteger(STORED_XP_NBT, this.bookMaxStorage - 1);
		ItemStack stackDepleted = new ItemStack(item, 1, this.getMaxDamage() - 1);
		stackDepleted.setTagCompound(new NBTTagCompound());
		stackDepleted.getTagCompound().setInteger(STORED_XP_NBT, 0);
		list.add(stackFull);
		list.add(stackDepleted);
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ClientProxy.BOOK_TEXTURE);
	}*/
}

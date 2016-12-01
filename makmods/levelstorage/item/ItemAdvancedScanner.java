package makmods.levelstorage.item;

import java.util.ArrayList;
import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAdvancedScanner extends Item implements IElectricItem, IHasRecipe {

	public static final int TIER = 2;
	public static final int STORAGE = 100000;
	public static final int COOLDOWN_PERIOD = 20;
	public static final int ENERGY_PER_USE = 10000;
	private static final int RADIUS = 16;

	public static final String NBT_COOLDOWN = "cooldown";

	public ItemAdvancedScanner(int id) {
		super();
		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		ItemStack glassFiber = IC2Items.getItem("cable", "type:glass,insulation:0");
		ItemStack advScanner = new ItemStack(LSBlockItemList.itemAdvScanner);
		Recipes.advRecipes.addRecipe(advScanner, "aca", "asa", "ggg", 'g', glassFiber, 'a',
				IC2ItemsShortcut.ADV_CIRCUIT, 'c', IC2ItemsShortcut.ENERGY_CRYSTAL, 's',
				IC2Items.getItem("advanced_scanner"));

	}

	public static void verifyStack(ItemStack stack) {
		// Just in case... Whatever!
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("charge")) {
				stack.getTagCompound().setInteger("charge", 0);
			}
		}
	}

	// TODO: refactor this later with the NBTHelper.
	public static void setNBTInt(ItemStack stack, String name, int value) {
		verifyStack(stack);
		stack.getTagCompound().setInteger(name, value);
	}

	public static int getNBTInt(ItemStack stack, String name) {
		verifyStack(stack);
		if (!stack.getTagCompound().hasKey(name)) {
			stack.getTagCompound().setInteger(name, 0);
		}
		return stack.getTagCompound().getInteger(name);
	}

	public void printMessage(String message, EntityPlayer player) {
		LevelStorage.proxy.messagePlayer(player, message, new Object[0]);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips, boolean adv) {
		String[] lines = I18n.format("tooltip.advScanner").split("\n");
		for (String line : lines) {
			tooltips.add("\2472" + line);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player,
			EnumHand hand) {
		if (!world.isRemote) {
			if (ElectricItem.manager.canUse(itemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(itemStack, ENERGY_PER_USE, player);
			} else
				return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
			if (!(getNBTInt(itemStack, NBT_COOLDOWN) == 0))
				return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
			setNBTInt(itemStack, NBT_COOLDOWN, COOLDOWN_PERIOD);

			ArrayList<ItemStack> blocksFound = new ArrayList<>();

			int playerX = (int) player.posX;
			int playerY = (int) player.posY;
			int playerZ = (int) player.posZ;

			for (int y = 0; y < (int) player.posY; y++) {
				for (int x = -(RADIUS / 2); x < (RADIUS / 2); x++) {
					for (int z = -(RADIUS / 2); z < (RADIUS / 2); z++) {
						IBlockState bs = world.getBlockState(new BlockPos(playerX + x, playerY + y, playerZ + z));
						ItemStack foundStack = new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs));
						blocksFound.add(foundStack);
					}
				}
			}

			this.printMessage("", player);
			this.printMessage("", player);
			this.printMessage("Found materials in " + RADIUS + "x" + RADIUS + " cubouid below you", player);
			this.printMessage("", player);
			ArrayList<String> names = new ArrayList<>();
			ArrayList<CollectedStatInfo> info = new ArrayList<>();
			for (ItemStack stack : blocksFound) {
				try {
					String currentName = stack.getDisplayName();
					if (!names.contains(currentName)) {
						names.add(currentName);
						info.add(new CollectedStatInfo(currentName, 1));
					} else {
						int amountAlreadyHas = 0;
						int indexAt = 0;
						for (int i = 0; i < info.size(); i++) {
							if (currentName.equals(info.get(i).name)) {
								amountAlreadyHas = info.get(i).amount;
								indexAt = i;
								break;
							}
						}
						info.remove(indexAt);
						info.add(new CollectedStatInfo(currentName, amountAlreadyHas + 1));
					}
				}
				// There will be a ton of these guys, let's ignore em
				catch (NullPointerException e) {
					continue;
				}
			}
			for (CollectedStatInfo i : info) {
				this.printMessage(i.name + " - " + i.amount, player);
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			verifyStack(par1ItemStack);
			if (getNBTInt(par1ItemStack, NBT_COOLDOWN) > 0) {
				setNBTInt(par1ItemStack, NBT_COOLDOWN, getNBTInt(par1ItemStack, NBT_COOLDOWN) - 1);
			}
		}
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
		return 2000;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> stacks) {
		ItemStack stack = new ItemStack(this, 1);
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		stacks.add(stack);
		stacks.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public void registerIcons(IconRegister
	 * par1IconRegister) { this.itemIcon = par1IconRegister
	 * .registerIcon(ClientProxy.ADV_SCANNER_TEXTURE); }
	 */
	public class CollectedStatInfo {
		public int amount;
		public String name;

		public CollectedStatInfo(String name, Integer amount) {
			this.name = name;
			this.amount = amount;
		}
	}
}

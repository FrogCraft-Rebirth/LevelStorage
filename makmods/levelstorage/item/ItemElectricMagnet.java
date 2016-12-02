package makmods.levelstorage.item;

import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.logic.util.SimpleMode;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemElectricMagnet extends Item implements IElectricItem, IHasRecipe {

	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_TICK = 10;
	public static final int TURN_ON_OFF_COOLDOWN = 10;
	public static final double RANGE = 32;

	public ItemElectricMagnet(int id) {
		super();
		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
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
		return 1000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ELECTRIC_MAGNET_TEXTURE);
	}*/

	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List<String> tooltips, boolean adv) {
		// par3List.add("\2472"
		// + StatCollector.translateToLocal("tooltip.electricMagnet"));
		tooltips.add(getStringState(stack));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			Cooldownable.onUpdate(par1ItemStack, TURN_ON_OFF_COOLDOWN);
			writeInitialMode(par1ItemStack);

			if (SimpleMode.readFromNBT(par1ItemStack.getTagCompound()).boolValue) {
				if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_TICK)) {
					boolean used = false;
					for (Object obj : par2World.loadedEntityList) {
						if (obj instanceof EntityItem
								|| obj instanceof EntityXPOrb) {
							Entity item = (Entity) obj;
							double distanceX = Math.abs(par3Entity.posX
									- item.posX);
							double distanceY = Math.abs(par3Entity.posY
									- item.posY);
							double distanceZ = Math.abs(par3Entity.posZ
									- item.posZ);
							double distanceTotal = distanceX + distanceY
									+ distanceZ;
							if (distanceTotal < RANGE) {
								used = true;
								item.setPosition(par3Entity.posX,
										par3Entity.posY, par3Entity.posZ);
							}
						}
					}
					if (par3Entity instanceof EntityLivingBase)
						if (used)
							ElectricItem.manager.use(par1ItemStack,
									ENERGY_PER_TICK,
									(EntityLivingBase) par3Entity);
				}
			}
		}
	}

	public static void writeInitialMode(ItemStack stack) {
		NBTHelper.checkNBT(stack);
		if (!stack.getTagCompound().hasKey(SimpleMode.NBT_COMPOUND_NAME)) {
			SimpleMode.OFF.writeToNBT(stack.getTagCompound());
		}
	}

	public String getStringState(ItemStack par1ItemStack) {
		return "Active: "
				+ (SimpleMode.readFromNBT(par1ItemStack.getTagCompound()).boolValue ? TextFormatting.DARK_GREEN
						+ "yes"
						: TextFormatting.DARK_RED + "no");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			writeInitialMode(itemStack);
			SimpleMode.readFromNBT(itemStack.getTagCompound()).getReverse().writeToNBT(itemStack.getTagCompound());
			LevelStorage.proxy.messagePlayer(player, getStringState(itemStack), new Object[0]);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemElectricMagnet),
				"cc ", "cic", " cb",
				'c', "plateCopper",
				'i', "plateIron",
				'b', IC2Items.getItem("crafting", "small_power_unit"));
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

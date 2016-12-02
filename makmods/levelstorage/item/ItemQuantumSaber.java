package makmods.levelstorage.item;

import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemQuantumSaber extends Item implements IElectricItem, IHasRecipe {

	public static final int TIER = 3;
	public static final int DAMAGE = 30;
	public static final int STORAGE = 1000000;
	public static final int ENERGY_PER_USE = 1000;

	public ItemQuantumSaber(int id) {
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
		return 10000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.QUANTUM_SABER_TEXTURE);
	}*/

	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase mob, EntityLivingBase player) {
		if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
			ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE, player);
			if (player instanceof EntityPlayer) {
				mob.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), DAMAGE);
			}
		}
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips, boolean adv) {

	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemQuantumSaber), 
				"ai ", "ai ", "cnl",
				'i', IC2ItemsShortcut.IRIDIUM_PLATE, 
				'a', IC2ItemsShortcut.ADV_ALLOY,
				'c', IC2ItemsShortcut.ADV_CIRCUIT, 
				'l', IC2ItemsShortcut.LAPOTRON_CRYSTAL, 
				'n', IC2Items.getItem("nano_saber"));

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

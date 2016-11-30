package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEnergeticEnrichedMatterOrb extends Item implements
        IElectricItem, IHasRecipe {

	// I WANT 4!
	public static final int TIER = 4;
	public static final int STORAGE = 40 * 1000 * 1000;

	public ItemEnergeticEnrichedMatterOrb(int id) {
		super();

		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemStorageFourtyMillion), "lsl", "sis", "lsl",
		        Character.valueOf('l'), IC2ItemsShortcut.LAPOTRON_CRYSTAL, Character
		                .valueOf('s'), new ItemStack(
		                LSBlockItemList.itemSuperconductor).copy(), Character
		                .valueOf('i'), IC2ItemsShortcut.IRIDIUM_PLATE);
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
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
		return 100000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ENERGETIC_ENRICHED_MATTER_ORB_TEXTURE);
	}*/

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
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

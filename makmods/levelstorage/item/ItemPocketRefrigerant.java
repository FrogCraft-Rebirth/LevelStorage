package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPocketRefrigerant extends Item implements IElectricItem, IHasRecipe {

	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_USE = 500;

	public static final int TIER = 1;

	public ItemPocketRefrigerant(int id) {
		super();

		this.setMaxDamage(27);
		this.setNoRepair();
		this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(LSBlockItemList.itemPocketRefrigerant),
				new ItemStack(Blocks.ICE), new ItemStack(Blocks.ICE), new ItemStack(Blocks.ICE),
				new ItemStack(Blocks.ICE), IC2Items.getItem("advancedCircuit"), IC2Items.getItem("reBattery"));
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(LSBlockItemList.itemPocketRefrigerant),
				new ItemStack(Blocks.ICE), new ItemStack(Blocks.ICE), new ItemStack(Blocks.ICE),
				new ItemStack(Blocks.ICE), IC2Items.getItem("advancedCircuit"), IC2Items.getItem("chargedReBattery"));
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if (par3Entity.isBurning()) {
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE, (EntityLivingBase) par3Entity);
				par3Entity.extinguish();
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
		return 10000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.POCKET_REFRIGERANT_TEXTURE);
	}*/

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
		        true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}

}

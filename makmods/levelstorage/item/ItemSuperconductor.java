package makmods.levelstorage.item;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSConfig;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemSuperconductor extends Item implements IHasRecipe {

	public ItemSuperconductor(int id) {
		super();
		this.setNoRepair();
		this.setMaxStackSize(16);
		this.setCreativeTab(LSCreativeTab.instance);
		OreDictionary.registerOre("itemSuperconductor", this);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemSuperconductor, LSConfig.superconductorCraftingAmount), 
				"ccc", "iai", "ccc", 
				'c', IC2Items.getItem("cable", "type:glass,insulation:0"),
				'i', IC2ItemsShortcut.IRIDIUM_PLATE, 
				'a', IC2ItemsShortcut.ADV_MACHINE);

	}

	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.EPIC;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ITEM_SUPERCONDUCTOR_TEXTURE);
	}*/
}

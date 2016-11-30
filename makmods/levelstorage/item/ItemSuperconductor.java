package makmods.levelstorage.item;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

public class ItemSuperconductor extends Item implements IHasRecipe {

	public ItemSuperconductor(int id) {
		super();
		this.setNoRepair();
		this.setMaxStackSize(16);
		this.setCreativeTab(LSCreativeTab.instance);
		Property pEnable = LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"superConductorOreDictCompatible", true);
		pEnable.setComment("Determines whether or not LevelStorage's superconductors are oredict-compatible with other superconductors (e.g. GraviSuite or GregTech)");
		if (pEnable.getBoolean(true))
			OreDictionary.registerOre("itemSuperconductor", this);
	}

	public void addCraftingRecipe() {
		Property pOutput = LevelStorage.configuration
				.get(Configuration.CATEGORY_GENERAL,
						"superConductorRecipeOutput", 6);
		pOutput.setComment("Determines how much superconductors you get from one recipe");

		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemSuperconductor, pOutput.getInt(6)), "ccc",
				"iai", "ccc", 
				'c', ic2.api.item.IC2Items.getItem("glassFiberCableItem"),
				'i', IC2ItemsShortcut.IRIDIUM_PLATE, 
				'a', ic2.api.item.IC2Items.getItem("advancedMachine"));

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

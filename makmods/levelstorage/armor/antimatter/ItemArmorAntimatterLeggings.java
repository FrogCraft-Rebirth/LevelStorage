package makmods.levelstorage.armor.antimatter;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.item.ItemStack;

public class ItemArmorAntimatterLeggings extends ItemArmorAntimatterBase implements IHasRecipe {
	
	public ItemArmorAntimatterLeggings(int id) {
		super(id, ItemArmorAntimatterBase.LEGGINGS);
	}
	
	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemArmorAntimatterLeggings), 
				"ici", "pap", "pep", 
				'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(), 
				'e', new ItemStack(LSBlockItemList.itemAntimatterCrystal), 
				'a', new ItemStack(LSBlockItemList.itemSupersonicLeggings), 
				'c', IC2Items.getItem("te", "teleporter"), 
				'i', IC2ItemsShortcut.IRIDIUM_PLATE);
	}
}

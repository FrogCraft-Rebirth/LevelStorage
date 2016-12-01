package makmods.levelstorage.proxy;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleRecipeAdder {

	public static void add3by3(ItemStack output, ItemStack input) {
		ItemStack ci = input.copy();
		Recipes.advRecipes.addRecipe(output.copy(), "ccc", "ccc", "ccc", 'c', ci);
	}

	public static void addChromeRecipes() {
		FurnaceRecipes.instance().addSmeltingRecipe(SimpleItemShortcut.DUST_CHROME.getItemStack(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		add3by3(SimpleItemShortcut.DUST_CHROME.getItemStack(),
				SimpleItemShortcut.DUST_TINY_CHROME.getItemStack());
		Recipes.macerator.addRecipe(new RecipeInputItemStack(SimpleItemShortcut.INGOT_CHROME.getItemStack().copy()), 
				null, false, SimpleItemShortcut.DUST_CHROME.getItemStack());
		FurnaceRecipes.instance().addSmeltingRecipe(SimpleItemShortcut.CRUSHED_CHROME_ORE.getItemStack(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		FurnaceRecipes.instance().addSmeltingRecipe(SimpleItemShortcut.PURIFIED_CHROME_ORE.getItemStack(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		// 3 outputs
		ItemStack oreWashingOutput1 = SimpleItemShortcut.PURIFIED_CHROME_ORE
				.getItemStack().copy();
		ItemStack oreWashingOutput2 = SimpleItemShortcut.DUST_TINY_CHROME
				.getItemStack().copy();
		oreWashingOutput2.stackSize = 2;
		ItemStack oreWashingOutput3 = IC2Items.getItem("stoneDust").copy();
		Recipes.oreWashing.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.CRUSHED_CHROME_ORE.getItemStack()), null, false,
				oreWashingOutput1, oreWashingOutput2, oreWashingOutput3);
		// 3 outputs
		ItemStack centrifugeOutput1 = SimpleItemShortcut.DUST_CHROME
				.getItemStack().copy();
		ItemStack centrifugeOutput2 = SimpleItemShortcut.DUST_TINY_OSMIUM
				.getItemStack();
		centrifugeOutput2.stackSize = 1;
		ItemStack centrifugeOutput3 = SimpleItemShortcut.TINY_IRIDIUM_DUST
				.getItemStack().copy();
		Recipes.centrifuge.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.PURIFIED_CHROME_ORE.getItemStack()), null, false,
				centrifugeOutput1, centrifugeOutput2, centrifugeOutput3);
		ItemStack irBit = SimpleItemShortcut.TINY_IRIDIUM_DUST.getItemStack().copy();
		irBit.stackSize = 9;
		Recipes.compressor.addRecipe(new RecipeInputItemStack(irBit), null, false, IC2Items.getItem("iridiumOre").copy());
		Recipes.metalformerRolling.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy()), null, false,
				SimpleItemShortcut.PLATE_CHROME.getItemStack().copy());
		Recipes.advRecipes.addShapelessRecipe(SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(),
				SimpleItemShortcut.ANTIMATTER_GLOB.getItemStack(),
				IC2ItemsShortcut.IRIDIUM_PLATE,
				SimpleItemShortcut.ANTIMATTER_GLOB.getItemStack());
		// TODO: Move it to somewhere else
		// addScannerRecipe(SimpleItemShortcut.DUST_TINY_OSMIUM.getItemStack(), 20000, 2000000);
		LogHelper.finest("Successfully complete adding Simple Recipes");
		// Recipes.oreWashing
	}

	/*// This should be auto populated
	public static void addScannerRecipe(ItemStack output, int recordedAmountUU, int recordedAmountEU) {
		if (output == null)
			return;
		NBTTagCompound metadata = new NBTTagCompound();

		metadata.setInteger(AMOUNT_UU, recordedAmountUU);
		metadata.setInteger(AMOUNT_EU, recordedAmountEU);

		
		Recipes.Scanner.addRecipe(new RecipeInputItemStack(output), metadata, new ItemStack[0]);
	}*/

	public static final String AMOUNT_EU = "recordedAmountEU";
	public static final String AMOUNT_UU = "recordedAmountUU";

	public static void addRecipesAfterMUIFinished() {
		ItemStack crushedChromiteOre = SimpleItemShortcut.CRUSHED_CHROME_ORE
				.getItemStack().copy();
		crushedChromiteOre.stackSize = 2;
		Recipes.macerator.addRecipe(new RecipeInputItemStack(new ItemStack(
				LSBlockItemList.blockChromiteOre)), null, false, crushedChromiteOre);
		addChromeRecipes();
		ItemStack outp = new ItemStack(SimpleItems.instance, 1,
				SimpleItemShortcut.IV_GENERATOR_UPGRADE.ordinal());
		Recipes.advRecipes.addShapelessRecipe(outp,
				SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(),
				IC2Items.getItem("overclockerUpgrade"), new ItemStack(
						LSBlockItemList.blockMassMelter));
	}

	public static void addSimpleCraftingRecipes() {
		// Osmiridium alloy -> osmiridium plate
		ItemStack rec1 = SimpleItemShortcut.OSMIRIDIUM_ALLOY.getItemStack();
		rec1.stackSize = 4;
		Recipes.compressor.addRecipe(new RecipeInputOreDict(
				"itemOsmiridiumAlloy"), null, false,
				SimpleItemShortcut.OSMIRIDIUM_PLATE.getItemStack());
		// 4 tiny osmium dusts -> 1 dust
		GameRegistry.addRecipe(SimpleItemShortcut.DUST_OSMIUM.getItemStack(),
				"SS", "SS", Character.valueOf('S'),
				SimpleItemShortcut.DUST_TINY_OSMIUM.getItemStack());

		// Osmium dust -> osmium ingot
		ItemStack osmIngot = SimpleItemShortcut.INGOT_OSMIUM.getItemStack();
		ItemStack osmDust = SimpleItemShortcut.DUST_OSMIUM.getItemStack();
		FurnaceRecipes.instance().addSmeltingRecipe(osmDust, osmIngot, 20.0F);

		// Osmium Ingots + Iridium Ingots = Osmiridium Alloy
		Recipes.advRecipes.addRecipe(
				SimpleItemShortcut.OSMIRIDIUM_ALLOY.getItemStack(), "OOO",
				"III", Character.valueOf('O'), "ingotOsmium",
				Character.valueOf('I'), "ingotIridium");

		// Iridium Ore -> Iridium Ingot
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"addIridiumOreToIngotCompressorRecipe", true).getBoolean(true)) {
			try {
				Recipes.compressor.addRecipe(new RecipeInputItemStack(IC2Items.getItem("iridiumOre")),
						null, false, SimpleItemShortcut.INGOT_IRIDIUM.getItemStack());
			} catch (Throwable t) {
				LogHelper.warning("Failed to add Iridium ore -> ingot recipe. Fallbacking.");
				t.printStackTrace();
			}
		}

		Recipes.advRecipes.addRecipe(
				OreDictionary.getOres("itemAntimatterTinyPile").get(0).copy(),
				"ppp", "ppp", "ppp", Character.valueOf('p'),
				"itemAntimatterMolecule");
		Recipes.advRecipes.addRecipe(OreDictionary
				.getOres("itemAntimatterGlob").get(0).copy(), "ppp", "ppp",
				"ppp", Character.valueOf('p'), "itemAntimatterTinyPile");
		Recipes.advRecipes.addRecipe(
				SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack()
						.copy(), " a ", "ana", " a ", Character.valueOf('a'),
				SimpleItemShortcut.ANTIMATTER_GLOB.getItemStack(), Character
						.valueOf('n'), new ItemStack(Items.NETHER_STAR).copy());
		Recipes.advRecipes.addShapelessRecipe(
				SimpleItemShortcut.JETPACK_ACCELERATOR.getItemStack(),
				IC2Items.getItem("overclockerUpgrade"),
				IC2ItemsShortcut.CARBON_PLATE.copy(), IC2ItemsShortcut.CARBON_PLATE.copy(),
				IC2ItemsShortcut.ADV_CIRCUIT.copy());

	}

}

package makmods.levelstorage.registry;

import java.util.AbstractMap;
import java.util.ArrayList;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSConfig;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

public class XpStackRegistry {

	public static final XpStackRegistry instance = new XpStackRegistry();

	private XpStackRegistry() {
	}

	public ArrayList<XpStack> entries = new ArrayList<XpStack>();
	public static final String XP_REGISTRY_CATEGORY = "xpRegistry";

	public static final AbstractMap.SimpleEntry<Integer, Integer> XP_EU_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
			1, 256); // 1 XP = 256 EU
	public static final AbstractMap.SimpleEntry<Integer, Integer> UUM_XP_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
			1, 130); // 1 UUM = 130 XP

	public static final int ORE_DICT_NOT_FOUND = -1;

	public void initCriticalNodes() {
		if (LSConfig.easyBronzeIngotRecipe) {
			ItemStack bronze = OreDictionary.getOres("ingotBronze").get(0).copy();
			bronze.stackSize = 4;
			Recipes.advRecipes.addShapelessRecipe(bronze, "ingotCopper", "ingotCopper", "ingotCopper", "ingotTin");
		}
		this.pushToRegistryWithConfig(new XpStack(new ItemStack(Items.REDSTONE),
				4));
		this.pushToRegistryWithConfig(new XpStack(new ItemStack(
				Items.QUARTZ), 1));
		this.pushToRegistryWithConfig(new XpStack(
				new ItemStack(Items.GOLD_INGOT), 16));
		this.pushToRegistryWithConfig(new XpStack(
				new ItemStack(Items.ENDER_PEARL), 192));
		this.pushToRegistryWithConfig(new XpStack(
				new ItemStack(Items.GLOWSTONE_DUST), 8));
		this.pushToRegistryWithConfig(new XpStack(new ItemStack(Items.DIAMOND),
				256));
		this.pushToRegistryWithConfig(new XpStack(
				new ItemStack(Items.NETHER_STAR), 8192));

		// dummy for debug
		// this.pushOreToRegistry("ingotGold", 1);

		if (LevelStorage.DETECT_GT) {
			this.pushOreToRegistry("dustDiamond", 512);
			this.pushOreToRegistry("dustTinyDiamond", 128);
			this.pushOreToRegistry("dustPlatinum", 2048);
			this.pushOreToRegistry("dustTinyPlatinum", 512);
			this.pushOreToRegistry("dustPlutonium", 1536);
			this.pushOreToRegistry("dustTinyPlutonium", 1536 / 4);
		}

		// this.pushOreToRegistry("dustDiamond", 512);
	}

	public boolean containsStack(ItemStack stack) {
		// NO CME
		XpStack[] stacks = (XpStack[]) XpStackRegistry.instance.entries
				.toArray(new XpStack[XpStackRegistry.instance.entries.size()]);
		for (XpStack xpstack : stacks) {
			if (xpstack.stack.getItem() == stack.getItem()
					&& xpstack.stack.getItemDamage() == stack.getItemDamage())
				return true;
		}
		return false;
	}

	public int getStackValue(ItemStack stack) {
		// if (!containsStack(stack))
		// return 0;
		XpStack[] stacks = (XpStack[]) XpStackRegistry.instance.entries
				.toArray(new XpStack[XpStackRegistry.instance.entries.size()]);
		for (XpStack xpstack : stacks) {
			if (xpstack.stack.getItem() == stack.getItem()
					&& xpstack.stack.getItemDamage() == stack.getItemDamage())
				return xpstack.value;
		}
		return 0;
	}

	/**
	 * Very sneaky, right? :P
	 */

	public void printRegistry() {
		LogHelper.info("Starting printing the xp registry contents");
		for (XpStack s : this.entries) {
			LogHelper.info("\t#" + s.stack.getItem() + ":"
					+ s.stack.getItemDamage() + " - "
					+ s.stack.getDisplayName() + " - " + s.value + " (1 "
					+ s.stack.getDisplayName() + " = " + s.value + " XP)");
		}
	}

	public void pushToRegistry(XpStack stack) {
		FMLLog.info("Adding #" + stack.stack.getItem() + ":"
				+ stack.stack.getItemDamage() + " to the Xp Registry, value: "
				+ stack.value);
		this.entries.add(stack);
	}

	//This method is bad. I will replace the XPRegistry system with a xml system.
	//For now, I simply disable the whole xpstack config system.
	public void pushToRegistryWithConfig(XpStack stack) {
		/*Property property = LevelStorage.configuration
				.get(XP_REGISTRY_CATEGORY,
						stack.stack.getItem().getUnlocalizedName()
								.replace("item.", "").replace(".name", "")
								.replace("tile.", ""), stack.value);
		property.setComment("Set to -1 to disable");
		int value = property.getInt();
		if (value == -1) {
			LogHelper.warning("XP entry for item "
					+ CommonHelper.getNiceStackName(stack.stack)
					+ " is disabled");
			return;
		}
		FMLLog.info("Adding #" + stack.stack.getItem() + ":"
				+ stack.stack.getItemDamage() + " to the Xp Registry, value: "
				+ stack.value);
		this.entries.add(new XpStack(stack.stack, value));*/
	}

	public void pushOreToRegistry(String name, int value) {
		boolean exists = false;
		for (ItemStack stack : OreDictionary.getOres(name)) {
			this.pushToRegistry(new XpStack(stack, value));
			exists = true;
		}
		if (!exists) {
			FMLLog.warning(Reference.MOD_NAME
					+ ": failed to add ore to XP Registry - " + name);
		}
	}
}

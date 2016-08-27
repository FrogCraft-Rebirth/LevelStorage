package makmods.levelstorage.logic.util;

import makmods.levelstorage.LevelStorage;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictHelper {
	@Deprecated //Use OreDictionary#doesOreNameExist
	public static boolean oreExists(String name) {
		/*boolean exists = false;
		for (ItemStack stack : OreDictionary.getOres(name)) {
			exists = true;
		}*/
		return OreDictionary.doesOreNameExist(name);
	}

	public static final String ORE_DICT_NOT_FOUND = "Unknown";

	static {
		if (!OreDictionary.doesOreNameExist("oreCoal")) {
			if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
			        "addCoalOreToOreDict", true).getBoolean(true))
				OreDictionary.registerOre("oreCoal", Blocks.COAL_ORE);
		}
		// if (!oreExists("oreRedstone")) {
		// Redstone Glowing Ore is a separately another block,
		// so drill will treat redstone normal and redstone glowing blocks
		// as different while it shouldn't
		// this is why I add this option.
		// Other mods (i.e. GT, or forge in future) may correct that bug
		// And make things go crazy wrong, so just a fallback config option.
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
		        "addRedstoneOreToOreDict", true).getBoolean(true)) {
			OreDictionary.registerOre("oreRedstone", Blocks.REDSTONE_ORE);
			OreDictionary.registerOre("oreRedstone", Blocks.LIT_REDSTONE_ORE);
		}
		// }
	}

	public static String getOreName(ItemStack st) { //TODO
		return OreDictionary.getOreName(OreDictionary.getOreIDs(st)[0]);
	}

}

package makmods.levelstorage.logic.util;

import net.minecraft.item.ItemStack;
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

	//There used to be a static initializer for oreCoal and oreRedstone.
	//However, MiencraftForge has already built them in, so yah, dont do redundant job.
	//Even though the Blocks.LIT_REDSTONE_ORE does not has oredict.

	public static String getOreName(ItemStack st) { //TODO
		return OreDictionary.getOreName(OreDictionary.getOreIDs(st)[0]);
	}

}

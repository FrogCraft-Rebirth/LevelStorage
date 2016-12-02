package makmods.levelstorage.dimension;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;

public class LSDimensions {
	public static int ANTIMATTER_UNIVERSE_DIMENSION_ID;
	
	public static final WorldType ANTIMATTER_UNIVERSE = new WorldType("antimatter");
	
	public static String getAntimatterUniverseDimName() {
		return I18n.translateToLocal("dimension.antimatterUniverse.name");
	}

	public static void init() {
		ANTIMATTER_UNIVERSE_DIMENSION_ID = LevelStorage.configuration.get(
				"dimension", "antimatterUniverseDimId",
				DimensionManager.getNextFreeDimId()).getInt();
		LogHelper.info("Registering dimension #" + ANTIMATTER_UNIVERSE_DIMENSION_ID);
		//DimensionManager.registerProviderType(ANTIMATTER_UNIVERSE_DIMENSION_ID,
		//		WorldProviderAntimatterUniverse.class, false);
		//DimensionManager.registerDimension(ANTIMATTER_UNIVERSE_DIMENSION_ID,
		//		ANTIMATTER_UNIVERSE_DIMENSION_ID);
		
	}
}

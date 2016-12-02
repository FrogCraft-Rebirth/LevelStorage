package makmods.levelstorage;

import java.util.List;

import org.apache.logging.log4j.Logger;

import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.command.CommandChargeItems;
import makmods.levelstorage.init.Config;
import makmods.levelstorage.init.LSIMCHandler;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.proxy.LSKeyboard;
import makmods.levelstorage.registry.FlightRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
//import net.minecraftforge.fml.common.network.NetworkMod;
import net.minecraftforge.fml.relauncher.Side;

//@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "Forge@[9.10.0.804,);required-after:IC2")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:IC2@[2.0,)")
//@NetworkMod(channels = { Reference.MOD_ID, LSKeyboard.PACKET_KEYBOARD_CHANNEL,
//		Reference.CUSTOM_PACKET_CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class LevelStorage {

	@Instance(Reference.MOD_ID)
	public static LevelStorage instance;

	@SidedProxy(clientSide = "makmods.levelstorage.proxy.ClientProxy", serverSide = "makmods.levelstorage.proxy.CommonProxy")
	public static CommonProxy proxy;

	@SidedProxy(clientSide = "makmods.levelstorage.proxy.LSKeyboardClient", serverSide = "makmods.levelstorage.proxy.LSKeyboard")
	public static LSKeyboard keyboard;

	public static int itemLevelStorageBookSpace;
	public static Configuration configuration;
	public static boolean chargerOnlyUUM;
	public static boolean experienceRecipesOn;
	public static boolean fancyGraphics;
	public static boolean recipesHardmode = false;
	public static final String BALANCE_CATEGORY = "balance";
	public static final String RECIPES_CATEGORY = "recipes";
	public static final String PERFORMANCE_CATEGORY = "performance";
	public static Fluid IC2UUM;
	public static final String STORAGE_CATEGORY = "electricitemstorage";
	public static final String IDS_CATEGORY = "ids";
	public static Logger logger;
	public static float powerExplosionAntimatterBomb;
	private long initTimeMeter;

	public static boolean detectedGT = false;

	public static boolean isAnySolarModLoaded() {
		return Loader.isModLoaded("AdvancedSolarPanel") || detectedGT;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		initTimeMeter = System.currentTimeMillis();
		LogHelper.info("Pre-Initialization...");
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		configuration = config;
		configuration.load();
		Config.ACTIVE = true;
		LevelStorage.itemLevelStorageBookSpace = config.get(Configuration.CATEGORY_GENERAL, "bookCapacity", 1 << 14).getInt(); // 1 << 14 == 16384
		Property p = config.get(LevelStorage.BALANCE_CATEGORY, "chargerOnlyUsesUUM", true);
		p.setComment("If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM)");
		LevelStorage.chargerOnlyUUM = p.getBoolean(true);

		Property p2 = config.get(LevelStorage.BALANCE_CATEGORY, "experienceRecipesEnabled", true);
		p2.setComment("Whether or not experience recipes are enabled");
		LevelStorage.experienceRecipesOn = p2.getBoolean(true);
		Property p4 = config.get(LevelStorage.BALANCE_CATEGORY, "hardRecipes", false);
		p4.setComment("If set to true, armors (and other) will require hard-to-get materials (f.e. full set of armor will require 72 stacks of UUM)");
		LevelStorage.recipesHardmode = p4.getBoolean(false);

		Property p5 = config.get(LevelStorage.BALANCE_CATEGORY, "explosionPowerAntimatterBomb", false);
		p5.setComment("Explosion power of Antimatter Bomb, where TNT is 4");
		LevelStorage.powerExplosionAntimatterBomb = p5.getInt(100);
		proxy.preInit();
	}

	public static boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	
	public static boolean isRendering() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		// Clearing all the stuff, so we don't occasionally do bad things
		// like wasting energy for nothing.
		// WirelessPowerSynchronizerRegistry.instance.registry.clear();
		// WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();
		event.registerServerCommand(new CommandChargeItems());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		// WirelessPowerSynchronizerRegistry.instance.registry.clear();
		// WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();
		// ImmutableList list = ImmutableList.of(); //What?
	}

	@EventHandler
	public void onInterModComms(IMCEvent event) {
		List<IMCMessage> messages = event.getMessages();
		for (IMCMessage message : messages) {
			try {
				if (message.isStringMessage()) {
					String value = message.getStringValue();
					String key = message.key;
					LSIMCHandler.instance.handle(key, value);
				} else {
					throw new IllegalArgumentException("Value must be string!");
				}
			} catch (Exception e) {
				LogHelper.warning("Mod " + message.getSender()
						+ " sent an invalid FMLInterModComms message.");
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		LogHelper.info("Initialization...");
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LogHelper.info("Post-Initialization...");
		proxy.postInit();
		LogHelper.info("Initialization took "
				+ (System.currentTimeMillis() - initTimeMeter) + " ms.");
		//for (String s : FluidRegistry.getRegisteredFluids().keySet())
		//	System.out.println(s);
		IC2UUM = FluidRegistry.getFluid("uumatter");
	}

	public static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}
}

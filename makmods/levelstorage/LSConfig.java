package makmods.levelstorage;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.lib.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;

@Config(modid = Reference.MOD_ID, name = Reference.MOD_NAME)
public final class LSConfig {
    private LSConfig() {}

    public static enum LSConfigCategory {
        BALANCE(LevelStorage.BALANCE_CATEGORY),
        IV_REGISTRY(IVRegistry.IV_CATEGORY),
        GENERAL(Configuration.CATEGORY_GENERAL);

        final String name;
        LSConfigCategory(String name) {
            this.name = name;
        }
    }
    
    @Config.Comment("Self-explained: 3 copper ingots and one tin ingots. Forestry has such recipe.")
    public static boolean easyBronzeIngotRecipe = false;

    @Config.Comment("The level storage capacity of experience book.")
    @Config.LangKey("config.levelstorage.expBookCapacity")
    @Config.RangeInt(min = 0)
    public static int itemLevelStorageBookSpace = 16384; //Used to be 2^14, or 1 << 14
    
    @Config.Comment("Determines how much XP is consumed from XP tome when you try to enchant a book with it (default V while holding XP tome in your hand and having books in inventory")
    @Config.LangKey("config.levelstorage.")
    @Config.RangeInt(min = 1)
    public static int xpPerEnchantmentBook = 840;

    @Config.Comment("If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM))")
    public static boolean chargerOnlyUUM = true;

    @Config.Comment("Whether or not experience recipes are enabled")
    @Config.LangKey("config.levelstorage.enableExpRecipe")
    public static boolean experienceRecipesEnabled = true;

    @Config.Comment("If set to true, armors (and other) will require hard-to-get materials (e.g. full set of armor will require 72 stacks of UUM)")
    public static boolean recipesHardmode = false;

    @Config.Comment("Explosion power of Antimatter Bomb, where TNT is 4")
    @Config.LangKey("config.levelstoarge.explosionPowerAntimatterBomb")
    @Config.RangeInt(min = 0)
    public static int powerExplosionAntimatterBomb = 100;
    
    @Config.Comment("Speed at which IV Generators produce IV per tick.")
    @Config.LangKey("config.levelstorage.ivGenerationRate")
    @Config.RangeInt(min = 0)
    public static int ivGenerationRate = 20;
    
    @Config.Comment("")
    @Config.LangKey("config.levelstorage.oreDensityFactor")
    @Config.RangeInt(min = 1)
    public static int oreDensityFactor = 1;
    
    @Config.Comment("If set to false, wireless conductors will stop spawning lightnings")
    public static boolean conductorSpawnLightnings = true;
    
    @Config.Comment("If set to false, conductors will stop spawning particles (useful on servers, because every 40 ticks server will send 180 packets to all the clients)")
    public static boolean conductorSpawnParticles = true;
    
    @Config.Comment("")
    public static boolean enableArmorHUD = true;
    
    @Config.Comment("Maximum tunnel length for Atomic Disassemblers (power of 2, default = 7 = 128)")
    public static int atomicDisassemblerDigLengthExponent = 7;
    
    @Config.Comment("")
    public static boolean enableAtomicDisassemblerDamage = true;
    
    @Config.Comment("Speed of enhanced diamond drill (diamond drill = 16, default = 32)")
    @Config.LangKey("config.levelstorage.enchancedDiamondDrillSpeed")
    @Config.RangeInt(min = 1)
    public static int enchancedDiamondDrillSpeed = 32;
    
    @Config.Comment("Determines how many superconductors you get from one recipe")
    @Config.LangKey("config.levelstorage.superconductorCraftingAmount")
    @Config.RangeInt(min = 1, max = 64)
    public static int superconductorCraftingAmount = 6;
    
    @Config.Comment({
    	"Determines how many passes (\"attempts\") will be made.",
    	"Basically, the lower value, the faster minecraft will start, the higher value, the more items will be assigned.", 
    	"Set to -1 to completely disable.", 
    	"2 is the minimum requirement for semi-filled IV values.", 
    	"1 will cover the most straightforward recipes."
    })
    @Config.LangKey("config.levelstorge.dynLVMappingDepth")
    @Config.RangeInt(min = -1)
    public static int dynamicIVRegistryMappingDepth = -1;
    
    @Config.Comment("Self-explained. IC2 macerator will produce much more powder from blaze rod than it from manual crafting.")
    public static boolean disableBlazePowderExploit = true;
    
    @Config.Comment("Determines whether or not crafting recipe is enabled")
    @Config.LangKey("config.levelstorage.enableForcefieldChestplateRecipe")
    public static boolean enableForcefieldChestplateCraftingRecipe;
    
    @Config.Comment("Determines whether or not crafting recipe is enabled")
    @Config.LangKey("config.levelstorage.enableEnhancedLappackRecipe")
    public static boolean enableEnhancedLappackCraftingRecipe;
    
    @Config.Comment("Determines whether or not crafting recipe is enabled")
    @Config.LangKey("config.levelstorage.enableSupersonicLeggingsRecipe")
    public static boolean enableSupersonicLeggingsCraftingRecipe;
    
    @Config.Comment("Determines whether or not crafting recipe is enabled")
    @Config.LangKey("config.levelstorage.enableTeslaHelmetRecipe")
    public static boolean enableTeslaHelmetCraftingRecipe;
}

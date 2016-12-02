package makmods.levelstorage;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.lib.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;

@Config(modid = Reference.MOD_ID, name = Reference.MOD_NAME)
public final class LSConfig {
    private LSConfig() {
    }

    public static enum LSConfigCategory {
        BALANCE(LevelStorage.BALANCE_CATEGORY),
        IV_REGISTRY(IVRegistry.IV_CATEGORY),
        GENERAL(Configuration.CATEGORY_GENERAL);

        final String name;
        LSConfigCategory(String name) {
            this.name = name;
        }
    }

    @Config.Comment("The level storage capacity of experience book.")
    @Config.LangKey("config.levelstorage.expBookCapacity")
    @Config.RangeInt(min = 0)
    public static int itemLevelStorageBookSpace = 16384; //Used to be 2^14, or 1 << 14

    @Config.Comment("If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM))")
    public static boolean chargerOnlyUUM = true;

    @Config.Comment("Whether or not experience recipes are enabled")
    @Config.LangKey("config.levelstorage.enableExpRecipe")
    public static boolean experienceRecipesEnabled = true;

    //@Config.Comment("")
    //public static boolean fancyGraphics;

    @Config.Comment("If set to true, armors (and other) will require hard-to-get materials (e.g. full set of armor will require 72 stacks of UUM)")
    public static boolean recipesHardmode = false;

    @Config.Comment("Explosion power of Antimatter Bomb, where TNT is 4")
    @Config.LangKey("config.levelstoarge.explosionPowerAntimatterBomb")
    @Config.RangeInt(min = 0)
    public static int powerExplosionAntimatterBomb = 100;
}

package makmods.levelstorage.item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.tileentity.TileEntityIVGenerator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleItems extends Item {

	public static SimpleItems instance = null;

	private static final String UID = "simpleItems";

	public SimpleItems() {
		super();
		this.setHasSubtypes(true);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		initItems();
	}

	public static interface ITooltipSensitive {
		List<String> getTooltip(ItemStack is);
	}

	public void addInformation(ItemStack stack,
			EntityPlayer player, List<String> tooltips, boolean adv) {
		int meta = stack.getItemDamage();
		ITooltipSensitive tooltip = SimpleItemShortcut.values()[meta].tooltipHandler;
		if (tooltip != null) {
			tooltips.addAll(tooltip.getTooltip(stack));
		}
	}

	public enum SimpleItemShortcut {
		DUST_TINY_OSMIUM(0, "dustTinyOsmium", EnumRarity.UNCOMMON, false), // 0
		DUST_OSMIUM(1, "dustOsmium", EnumRarity.RARE, false), // 1
		OSMIRIDIUM_ALLOY(2, "itemOsmiridiumAlloy", EnumRarity.RARE, false), // 2
		OSMIRIDIUM_PLATE(4, "itemOsmiridiumPlate", EnumRarity.EPIC, false), // 3
		INGOT_OSMIUM(8, "ingotOsmium", EnumRarity.RARE, false), // 4
		INGOT_IRIDIUM(16, "ingotIridium", EnumRarity.UNCOMMON, false), // 5
		ULTIMATE_CIRCUIT(32, "itemUltimateCircuit", EnumRarity.RARE, false), // 6
		ENERGIZED_NETHER_STAR(64, "itemEnergizedStar", EnumRarity.EPIC, true), // 7
		ANTIMATTER_MOLECULE(128, "itemAntimatterMolecule", EnumRarity.RARE, false), // 8
		ANTMATTER_TINY_PILE(192, "itemAntimatterTinyPile", EnumRarity.EPIC, false), // 9
		ANTIMATTER_GLOB(256, "itemAntimatterGlob", EnumRarity.EPIC, false), // 10
		JETPACK_ACCELERATOR(384, "itemJetpackAccelerator", EnumRarity.UNCOMMON, false), // 11
		DUST_TINY_CHROME(512, "itemDustTinyChrome", EnumRarity.COMMON, false), // 13
		DUST_CHROME(513, "itemDustChrome", EnumRarity.COMMON, false), // 14
		CRUSHED_CHROME_ORE(514, "crushedChromiteOre", EnumRarity.COMMON, false), // 15
		PURIFIED_CHROME_ORE(515, "purifiedCrushedChromiteOre", EnumRarity.COMMON, false), // 16
		INGOT_CHROME(516, "ingotChrome", EnumRarity.COMMON, false), // 17
		PLATE_CHROME(517, "plateChrome", EnumRarity.COMMON, false),
		TINY_IRIDIUM_DUST(600, "dustTinyIridium", EnumRarity.COMMON, false), // 18
		PLATE_ANTIMATTER_IRIDIUM(518, "plateAntimatterIridium", EnumRarity.EPIC, false),
		IV_GENERATOR_UPGRADE(700, "craftingUpgradeIVGenerator", EnumRarity.EPIC, false,
				stack -> Arrays.asList("Increases IV Generator's speed by "
						+ (TileEntityIVGenerator.BUFF_IV_T * stack.stackSize) + " IV/t.")
				);
		final String name;
		final boolean hasEffect;
		final EnumRarity rarity;
		final int metadata;
		ITooltipSensitive tooltipHandler;

		SimpleItemShortcut(int metadata, String name, EnumRarity rarity, boolean hasEffect) {
			this.name = name;
			this.rarity = rarity;
			this.metadata = metadata;
			this.hasEffect = hasEffect;
			tooltipHandler = null;
		}

		SimpleItemShortcut(int metadata, String name, EnumRarity rarity, boolean hasEffect, ITooltipSensitive tooltipHandler) {
			this(metadata, name, rarity, hasEffect);
			this.tooltipHandler = tooltipHandler;
		}

		public String getName() {
			return name;
		}

		public boolean hasEffect() {
			return hasEffect;
		}

		public EnumRarity getRarity() {
			return rarity;
		}

		public String toString() {
			return getName();
		}

		public int getMetadata() {
			return metadata;
		}

		public ItemStack getItemStack() {
			return new ItemStack(SimpleItems.instance, 1, metadata)
					.copy();
		}
	}

	public void initItems() {
		SimpleItemShortcut[] vals = SimpleItemShortcut.values();
		for (SimpleItemShortcut val : vals)
			addItem(val.metadata, val.getName(), val.getRarity(),
					val.hasEffect());
	}

	// Meta <==> SimpleItemData
	private Map<Integer, SimpleItemData> mapping = Maps.newHashMap();

	private static class SimpleItemData {
		//public Icon icon;
		public String unlocalizedName;
		public EnumRarity rarity;
		public boolean hasEffect;
	}

	public void addItem(int meta, String name, EnumRarity rarity, boolean hasEffect) {
		if (mapping.containsKey(meta))
			throw new RuntimeException(
					"SimpleItems: addItem(): mapping already contains item with metadata ("
							+ name + ")");
		SimpleItemData data = new SimpleItemData();
		data.rarity = rarity;
		data.unlocalizedName = name;
		data.hasEffect = hasEffect;
		mapping.put(meta, data);
		ItemStack currStack = new ItemStack(this, 1, meta);
		OreDictionary.registerOre(name, currStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		int meta = stack.getItemDamage();
		if (!mapping.containsKey(meta))
			return false;
		return mapping.get(meta).hasEffect;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		int meta = par1ItemStack.getItemDamage();
		if (!mapping.containsKey(meta))
			return EnumRarity.COMMON;
		return mapping.get(meta).rarity;
	}

	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		return mapping.get(meta).unlocalizedName;
	}
/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister) {
		for (Entry<Integer, SimpleItemData> entry : mapping.entrySet()) {
			String name = entry.getValue().unlocalizedName;
			entry.getValue().icon = iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(name));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int meta) {
		if (!mapping.containsKey(meta))
			return null;
		try {
			return (Icon) mapping.get(meta).icon;
		} catch (Throwable t) {
			return null;
		}
	}*/

	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		mapping.keySet().forEach(meta -> list.add(new ItemStack(item, 1, meta)));
	}

}

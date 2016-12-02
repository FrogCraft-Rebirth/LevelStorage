package makmods.levelstorage.iv;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ic2.api.item.IC2Items;
import makmods.levelstorage.LSConfig;
import makmods.levelstorage.iv.parsers.IRecipeParser;
import makmods.levelstorage.iv.parsers.IVRecipeParser;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

/**
 * The heart of LevelStorage's IV system
 * 
 * @author mak326428
 * 
 */
public class IVRegistry {
	public static final IVRegistry instance = new IVRegistry();
	public static final int NOT_FOUND = -1;
	public List<IVItemStackEntry> itemStackEntries = Lists.newArrayList();
	public List<IVOreDictEntry> oreDictEntries = Lists.newArrayList();
	public List<IVFluidEntry> fluidEntries = Lists.newArrayList();
	public static final boolean DEBUG = false;

	/**
	 * Key - IV <br />
	 * Value - Fluid in mB
	 */
	public static final AbstractMap.SimpleEntry<Integer, Integer> IV_TO_FLUID_CONVERSION = new SimpleEntry<Integer, Integer>(
			10, 1);

	public static final String IV_CATEGORY = "iv";

	public static Map<String, ItemStack> nameToItemStackMap = Maps.newHashMap();
	public static Map<ItemStack, String> itemStackToNameMap = Maps.newHashMap();

	public void recognizeOreDict() {
		try {
			String[] names = OreDictionary.getOreNames();
			for (String s : names) {
				try {
					if (DEBUG)
						LogHelper.info("OreDict entry: " + s);
					List<ItemStack> stacksForGiven = OreDictionary.getOres(s);
					for (ItemStack st : stacksForGiven) {
						if (st == null)
							continue;

						nameToItemStackMap.put(s, st);
						itemStackToNameMap.put(st, s);
					}

				} catch (Exception e) {
					LogHelper.severe("Exception trying to parse OreDict by IV registry. This is fatal error and unrecoverable.");
					throw new RuntimeException(e);
				}
			}
		} catch (Throwable e) {
			LogHelper.severe("Exception when trying to dynamically allocate more ores to generate");
			e.printStackTrace();
		}
	}

	/**
	 * Used to prevent CMEs.
	 * 
	 * @return Exact copy of entries (mutable)
	 */
	public List<IVEntry> copyRegistry() {
		List<IVEntry> newList = Lists.newArrayList();
		for (IVOreDictEntry entry : oreDictEntries)
			newList.add(entry);
		for (IVItemStackEntry entry : itemStackEntries)
			newList.add(entry);
		return newList;
	}

	private IVRegistry() {
		;
	}

	public void init() {
		DO_CACHING = true;
		initCriticalNodes();
		recognizeOreDict();
		parseDynamically();
		printContents();
	}

	public void runParser(IRecipeParser parser) {
		long startTime = System.currentTimeMillis();
		parser.parse();
		LogHelper.info(parser.getClass().getSimpleName() + " took "
				+ (System.currentTimeMillis() - startTime) + "ms.");
	}

	public void parseDynamically() {
		runParser(new IVRecipeParser());

	}

	public void printContents() {
		LogHelper.info("Printing IVRegistry contents:");
		List<IVEntry> entries = copyRegistry();
		for (IVEntry entry : entries) {
			if (entry instanceof IVOreDictEntry) {
				IVOreDictEntry ode = (IVOreDictEntry) entry;
				LogHelper.info("\tOre Dictionary: " + ode.getName()
						+ ", value: " + ode.getValue());
			} else if (entry instanceof IVItemStackEntry) {
				IVItemStackEntry ise = (IVItemStackEntry) entry;
				LogHelper.info("\tItemStack: "
						+ ise.getStack().getDisplayName() + ", value: "
						+ ise.getValue());
			}
		}
	}

	private void initCriticalNodes() {
		assign(new ItemStack(Items.DIAMOND), 8192);
		assign(new ItemStack(Blocks.COAL_ORE), 128);
		assign(new ItemStack(Items.COAL), 128);
		assign(new ItemStack(Items.COAL, 1, 1), 32);
		assign(new ItemStack(Blocks.LOG, 1, OreDictionary.WILDCARD_VALUE), 32);
		assign(new ItemStack(Blocks.LOG2, 1, OreDictionary.WILDCARD_VALUE), 32);
		assign(new ItemStack(Items.GOLD_INGOT), 2048);
		assign(new ItemStack(Items.REDSTONE), 64);
		assign(new ItemStack(Blocks.STONE), 1);
		assign(new ItemStack(Blocks.DIRT), 1);
		assign(new ItemStack(Blocks.SAND), 1);
		assign(new ItemStack(Items.IRON_INGOT), 256);
		assign(new ItemStack(Items.CLAY_BALL), 16);
		assign(new ItemStack(Items.WHEAT), 32);
		assign(new ItemStack(Items.STRING), 12);
		assign(new ItemStack(Blocks.OBSIDIAN), 64);
		assign(new ItemStack(Items.ENDER_PEARL), 1024);
		assign(new ItemStack(Items.BLAZE_ROD), 1536);
		assign(new ItemStack(Items.CARROT), 64);
		assign(new ItemStack(Items.POTATO), 64);
		assign(new ItemStack(Items.GLOWSTONE_DUST), 384);
		assign(new ItemStack(Items.DYE, 1, 4), 768);
		assign(new ItemStack(Items.LEATHER), 64);
		assign(new ItemStack(Items.EMERALD), 8192);
		assign(new ItemStack(Items.FEATHER), 48);
		assign(new ItemStack(Blocks.ICE), 1);
		assign(new ItemStack(Blocks.DRAGON_EGG), 2000000);
		assign(new ItemStack(Items.MELON), 16);
		assign(new ItemStack(Items.QUARTZ), 24);
		assign(new ItemStack(Items.SADDLE), 256);
		assign(new ItemStack(Items.NETHER_STAR), 524288);
		assign(IC2Items.getItem("misc_resource", "iridium_ore").copy(), 131072);
		assign(IC2Items.getItem("misc_resource", "resin").copy(), 24);
		assign(new ItemStack(Items.SKULL, 1, 1), 87381);
		assign(new ItemStack(Items.REEDS), 24);
		assign(new ItemStack(Blocks.SOUL_SAND), 49);
		assign(new ItemStack(Blocks.END_STONE), 4);
		assign(new ItemStack(Items.GUNPOWDER), 192);
		assign(new ItemStack(Blocks.COBBLESTONE), 1);
		assign(new ItemStack(Blocks.NETHERRACK), 1);
		assign(IC2Items.getItem("crafting", "industrial_diamond"), 8192);
		assign(IC2Items.getItem("nuclear", "small_uranium_235"), 1024);
		assign(IC2Items.getItem("nuclear", "uranium_238"), 204);
		if (LSConfig.disableBlazePowderExploit)
			assign(new ItemStack(Items.BLAZE_POWDER), 307);
		assign(new ItemStack(Items.FLINT), 4);
		assign(new ItemStack(Blocks.GRAVEL), 4);
		assign(new ItemStack(Items.GHAST_TEAR), 4096);
		assign(new ItemStack(Items.ROTTEN_FLESH), 32);
		assign(new ItemStack(Blocks.YELLOW_FLOWER), 16);
		assign(new ItemStack(Blocks.RED_FLOWER), 16);
		assignAll(ItemRecord.class, 16384);
		// assign(Item.arrow, 16);
		assign("ingotTin", 255);
		assign("crystalCertusQuartz", 1024);
		assign("gemCertusQuartz", 1024);
		assign("ingotChrome", 12288);
		assign("ingotSilver", 512);
		assign("ingotCopper", 85);
		assign("ingotBronze", 170);
		if (FluidRegistry.isFluidRegistered("oil"))
			assign(FluidRegistry.getFluid("oil"), 6);
		if (FluidRegistry.isFluidRegistered("bioethanol"))
			assign(FluidRegistry.getFluid("bioethanol"), 3);
		if (Loader.isModLoaded("gregtech_addon"))
			IVCrossMod.addGTValues();
		if (Loader.isModLoaded("AdvancedSolarPanel"))
			IVCrossMod.addASPValues();
	}

	public void assignFluid(Fluid f, int value) {
		/*String name = f.getUnlocalizedName();
		int valueActual = LevelStorage.configuration.get(IV_CATEGORY, name,
				value).getInt();
		if (valueActual > 0)
			fluidEntries.add(new IVFluidEntry(f, valueActual));*/
	}

	public void assign(Object obj, int value) {
		if (obj instanceof ItemStack)
			assignItemStack((ItemStack) obj, value);
		else if (obj instanceof String)
			assignOreDictionary((String) obj, value);
		else if (obj instanceof Item)
			assignItemStack(new ItemStack((Item) obj), value);
		else if (obj instanceof Block)
			assignItemStack(new ItemStack((Block) obj), value);
		else if (obj instanceof Fluid) {
			assignFluid((Fluid) obj, value);
		} else
			throw new RuntimeException(
					"IVRegistry.assign() - obj's type is incorrect ("
							+ obj.getClass().getCanonicalName() + ")");
	}

	public void assign(Block item, int value) {
		assignItemStack(new ItemStack(item), value);
	}
	
	private static <T> Iterable<T> toIterable(Iterator<T> iterator) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}

	public void assignAll(Class<?> baseClass, int value) {
		for (Block b : toIterable(Block.REGISTRY.iterator())) {
			if (b != null) {
				if (baseClass.isAssignableFrom(b.getClass()))
					assignItemStack(new ItemStack(b), value);
			}
		}
		for (Item b : toIterable(Item.REGISTRY.iterator())) {
			if (b != null) {
				if (baseClass.isAssignableFrom(b.getClass()))
					assignItemStack(new ItemStack(b), value);
			}
		}
	}

	public void assignItemStack(ItemStack stack, int value) {
		/*try {
			String id = stack.getItem().getRegistryName().toString();
			int meta = stack.getItemDamage();
			int valueActual = LevelStorage.configuration.get(IV_CATEGORY, id + ":" + meta, value,
					stack.hasDisplayName() ? stack.getDisplayName().replace(".name", "") : stack.getUnlocalizedName())
					.getInt();
			if (valueActual > 0)
				itemStackEntries.add(new IVItemStackEntry(stack.copy(), value));
		} catch (Exception e) {
			System.out.println("Invalid ItemStack detected! " + stack.toString());
		}*/
	}

	public void removeIV(Object obj) {
		Object toRemove = null;
		if (obj instanceof String) {
			String initialName = (String) obj;
			for (IVOreDictEntry entry : oreDictEntries) {
				if (entry.getName().equals(initialName)) {
					toRemove = entry;
					break;
				}
			}
		} else if (obj instanceof ItemStack) {
			ItemStack initialStack = (ItemStack) obj;
			for (IVItemStackEntry entry : itemStackEntries) {
				ItemStack entryStack = entry.getStack();
				if (entryStack.getItem() == initialStack.getItem()
						&& (entryStack.getItemDamage() == initialStack
								.getItemDamage() || entryStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
					toRemove = entry;
					break;
				}
			}
		}
		if (toRemove != null) {
			if (toRemove instanceof IVOreDictEntry)
				oreDictEntries.remove(toRemove);
			else
				itemStackEntries.remove(toRemove);
			IVRegistry.clearCache();
		} else
			LogHelper.severe("removeIV: obj's type is incorrect - "
					+ obj.getClass().getSimpleName());
	}

	public void assignItemStack_dynamic(ItemStack stack, int value) {
		itemStackEntries.add(new IVItemStackEntry(stack.copy(), value));
	}

	public void assignOreDict_dynamic(String name, int value) {
		oreDictEntries.add(new IVOreDictEntry(name, value));
	}

	public void assignOreDictionary(String name, int value) {
		/*int valueActual = LevelStorage.configuration.get(IV_CATEGORY, name,
				value).getInt();
		if (valueActual > 0) {
			List<ItemStack> sts = OreDictionary.getOres(name);
			// I am not sure what's wrong here but it seems like there is still invalid entries
			sts.stream().filter(stack -> stack.getItem() != null && stack.getItem().getRegistryName() != null);
			for (ItemStack st : sts)
				assignItemStack(st.copy(), value);
			oreDictEntries.add(new IVOreDictEntry(name, value));
		}*/
	}

	/**
	 * Respects stack size
	 * 
	 * @param st
	 *            ItemStack
	 * @return IV for the requested item
	 */
	public int getValueForItemStack(ItemStack st) {
		if (st == null)
			return NOT_FOUND;
		int baseValue = getValueFor(st);
		if (baseValue == NOT_FOUND)
			return NOT_FOUND;
		return baseValue * st.stackSize;
	}

	public static boolean hasValue(Object obj) {
		return getValue(obj) != NOT_FOUND;
	}

	public static int getValue(Object obj) {
		return instance.getValueFor(obj);
	}

	public static boolean DO_CACHING = false;

	private static Map<ItemStack, Integer> itemStackCache = Maps.newHashMap();
	private static Map<String, Integer> oreDictCache = Maps.newHashMap();

	public static void clearCache() {
		if (DEBUG) {
			LogHelper.info(String.format(
					"Clearing IV cache, %d ItemStack, %d OreDict",
					itemStackCache.size(), oreDictCache.size()));
		}
		itemStackCache.clear();
		oreDictCache.clear();
	}

	public int getValueFor_internal(Fluid fluid) {
		if (fluid == null)
			return NOT_FOUND;
		for (IVFluidEntry entry : fluidEntries) {
			if (entry.getFluid().getName().equals(fluid.getName()))
				return entry.getValue();
		}
		return NOT_FOUND;
	}

	public int getValueFor(Object obj) {
		if (!DO_CACHING) {
			if (obj instanceof String)
				return getValueFor_internal((String) obj);
			else if (obj instanceof ItemStack)
				return getValueFor_internal((ItemStack) obj);
			else if (obj instanceof Fluid)
				return getValueFor_internal((Fluid) obj);
		}
		if (obj instanceof String) {
			String odName = (String) obj;
			if (!oreDictCache.containsKey(odName))
				oreDictCache.put(odName, getValueFor_internal(odName));
			return oreDictCache.get(odName);
		} else if (obj instanceof ItemStack) {
			List<Integer> lst = Lists.newArrayList();
			if (!itemStackCache.containsKey(lst)) //Impossible to pass this check: ItemStack does not override equals method, must manually iterate all entries
				itemStackCache.put((ItemStack)obj, getValueFor_internal((ItemStack)obj));
			return itemStackCache.get(obj);
		} else if (obj instanceof Fluid) {
			return getValueFor_internal((Fluid) obj);
		} else
			return NOT_FOUND;
	}

	public int getValueFor_internal(String name) {
		for (IVOreDictEntry oreDictEntry : oreDictEntries) {
			if (oreDictEntry.getName().equals(name))
				return oreDictEntry.getValue();
			if (!nameToItemStackMap.containsKey(name))
				return NOT_FOUND;
			ItemStack toResolveIS = nameToItemStackMap.get(name);
			// System.out.println(toResolveIS);
			for (IVItemStackEntry ivItemStackEntry : itemStackEntries) {
				ItemStack stack = ivItemStackEntry.getStack();

				if (stack.getItem() == toResolveIS.getItem()
						&& (stack.getItemDamage() == toResolveIS
								.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE))
					return ivItemStackEntry.getValue();
			}
		}
		return NOT_FOUND;
	}

	public int getValueFor_internal(ItemStack objStack) {
		if (objStack == null)
			return NOT_FOUND;
		for (IVItemStackEntry itemStackEntry : itemStackEntries) {
			ItemStack iterationIS = itemStackEntry.getStack();
			if (objStack.getItem() == iterationIS.getItem()
					&& (iterationIS.getItemDamage() == objStack.getItemDamage() || iterationIS
							.getItemDamage() == OreDictionary.WILDCARD_VALUE))
				return itemStackEntry.getValue();
			String toResolveIS = null;
			for (Entry<ItemStack, String> entryIs : itemStackToNameMap
					.entrySet()) {
				ItemStack st = entryIs.getKey();
				if (st.getItem() == objStack.getItem()
						&& (st.getItemDamage() == objStack.getItemDamage() || st
								.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
					toResolveIS = entryIs.getValue();
					break;
				}
			}

			for (IVOreDictEntry oreDictEntry : oreDictEntries) {
				if (oreDictEntry.getName().equals(toResolveIS))
					return oreDictEntry.getValue();

			}
		}
		return NOT_FOUND;
	}
}

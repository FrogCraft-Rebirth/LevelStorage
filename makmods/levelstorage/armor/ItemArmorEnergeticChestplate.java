package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorEnergeticChestplate extends ItemArmor implements
		ISpecialArmor, IMetalArmor, IElectricItem, IHasRecipe {

	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ARMOR_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 30000;
	public static final int ENTITY_MAX_DISTANCE = 16;
	public static final int ENERGY_PER_TICK_ENTITIES = 100;

	public ItemArmorEnergeticChestplate(int id) {
		super(ItemArmor.ArmorMaterial.DIAMOND, LevelStorage.proxy
				.getArmorIndexFor(CommonProxy.SUPERSONIC_DUMMY), EntityEquipmentSlot.CHEST);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ItemStack playerGetArmor(EntityPlayer p) {
		InventoryPlayer inv = p.inventory;
		ItemStack found = null;

		for (ItemStack st : inv.armorInventory) {
			if (st != null
					&& st.getItem() instanceof ItemArmorEnergeticChestplate)
				found = st;
		}

		return found;
	}

	public static final String FORCEFIELD_NBT = "forcefield";

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		ArmorFunctions.extinguish(player, world);
	}

	public void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"enableForcefieldChestplateCraftingRecipe", true);
		p.setComment("Determines whether or not crafting recipe is enabled");
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
					LSBlockItemList.itemArmorEnergeticChestplate), "ttt",
					"iqi", "lil", Character.valueOf('t'), IC2Items.TESLA_COIL,
					Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
							.valueOf('q'), IC2Items.QUANTUM_CHESTPLATE,
					Character.valueOf('l'), new ItemStack(
							LSBlockItemList.itemStorageFourtyMillion));

		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.FORCEFIELD_CHESTPLATE_TEXTURE);
	}*/

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		if (source.isUnblockable())
			return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);

		double absorptionRatio = getBaseAbsorptionRatio()
				* getDamageAbsorptionRatio();
		int energyPerDamage = ENERGY_PER_DAMAGE;

		int damageLimit = energyPerDamage > 0 ? (int)(25
				* ElectricItem.manager.getCharge(armor) / energyPerDamage) : 0;

		return new ISpecialArmor.ArmorProperties(0, absorptionRatio,
				damageLimit);
	}

	private double getBaseAbsorptionRatio() {
		return 0.4D;
	}

	public double getDamageAbsorptionRatio() {
		return 1.1D;
	}

	public void damageArmor(EntityLivingBase entity, ItemStack stack,
			DamageSource source, int damage, int slot) {
		ElectricItem.manager.discharge(stack, damage * ENERGY_PER_DAMAGE,
				2147483647, true, false, false);
	}

	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (ElectricItem.manager.getCharge(armor) >= ENERGY_PER_DAMAGE) {
			return (int) Math.round(20.0D * getBaseAbsorptionRatio()
					* getDamageAbsorptionRatio());
		}
		return 0;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return true;
	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return 100000D;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(item, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
				true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}

	@Override
	public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
		return true;
	}

}

package makmods.levelstorage.armor;

import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorEnhancedLappack extends ItemArmor implements
        ISpecialArmor, IMetalArmor, IElectricItem, IHasRecipe {
	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ENH_LAPPACK_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 900;

	public ItemArmorEnhancedLappack(int id) {
		super(ItemArmor.ArmorMaterial.DIAMOND,
		        LevelStorage.proxy.getArmorIndexFor(CommonProxy.ENH_LAPPACK_DUMMY), EntityEquipmentSlot.CHEST);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	@Override
	public void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableEnhancedLappackCraftingRecipe", true);
		p.setComment("Determines whether or not crafting recipe is enabled");
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        LSBlockItemList.itemEnhLappack), "ccc", "lal", "apa",
			        Character.valueOf('l'), makmods.levelstorage.lib.IC2ItemsShortcut.ENERGY_CRYSTAL,
			        Character.valueOf('a'), makmods.levelstorage.lib.IC2ItemsShortcut.ADV_CIRCUIT, 
			        Character.valueOf('p'), IC2Items.getItem("lapPack"), 
			        Character.valueOf('c'), makmods.levelstorage.lib.IC2ItemsShortcut.CARBON_PLATE);
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ENHANCED_LAPPACK_TEXTURE);
	}*/

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
	        ItemStack armor, DamageSource source, double damage, int slot) {
		return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);
	}
/*
	private double getBaseAbsorptionRatio() {
		return 0.0D;
	}*/

	public double getDamageAbsorptionRatio() {
		return 0.0D;
	}

	public void damageArmor(EntityLivingBase entity, ItemStack stack,
	        DamageSource source, int damage, int slot) {

	}

	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
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
		return 3000;
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

package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorAntimatterBase extends ItemArmor implements
		ISpecialArmor, IElectricItem {

	public static int RENDER_ID;
	public static final int HELMET = 0;
	public static final int CHESTPLATE = 1;
	public static final int LEGGINGS = 2;
	public static final int BOOTS = 3;
	public static final int ENERGY_PER_DAMAGE = 40000;
	public static final int STORAGE = (int)Math.pow(10, 9);

	public static boolean BOOTS_EXPLOSION = true;
	// Used for simplifying port process, TODO: finish it
	public static EntityEquipmentSlot intToEquiSlot(int armorType) {
		return EntityEquipmentSlot.CHEST;
	}

	public ItemArmorAntimatterBase(int id, int armorType) {
		super(ItemArmor.ArmorMaterial.DIAMOND, RENDER_ID, intToEquiSlot(armorType));
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static final int EU_PER_TELEPORT = 300000;
	public static final int EU_PER_TICK_WATERWALK = 100;

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (this.armorType == EntityEquipmentSlot.CHEST)
			ArmorFunctions.extinguish(player, world);
		else if (this.armorType == EntityEquipmentSlot.FEET) {
			ArmorFunctions.jumpBooster(world, player, itemStack);
			ArmorFunctions.fly(ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK,
					player, itemStack, world);
			ArmorFunctions.bootsSpecialFly(player, world, itemStack);
		} else if (this.armorType == EntityEquipmentSlot.LEGS) {
			ArmorFunctions.speedUp(player, itemStack);
			ArmorFunctions
					.antimatterLeggingsFunctions(world, player, itemStack);
		} else if (this.armorType == EntityEquipmentSlot.HEAD) {
			ArmorFunctions.helmetFunctions(world, player, itemStack,
					ItemArmorTeslaHelmet.RAY_COST,
					ItemArmorTeslaHelmet.FOOD_COST);
		}
	}

	public double getDamageAbsorptionRatio() {
		if (this.armorType == EntityEquipmentSlot.CHEST)
			return 1.1D;
		return 1.0D;
	}

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		double absorptionRatio = getBaseAbsorptionRatio()
				* getDamageAbsorptionRatio();
		int energyPerDamage = ENERGY_PER_DAMAGE;

		int damageLimit = energyPerDamage > 0 ? (int)(25
				* ElectricItem.manager.getCharge(armor) / energyPerDamage) : 0;

		return new ISpecialArmor.ArmorProperties(0, absorptionRatio,
				damageLimit);
	}

	private double getBaseAbsorptionRatio() {
		switch (this.armorType) {
		case HEAD:
			return 0.15D;
		case CHEST:
			return 0.4D;
		case LEGS:
			return 0.3D;
		case FEET:
			return 0.15D;
		default:
			return 0.0D;
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
		if (this.armorType == HELMET)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_HELMET_TEXTURE);
		else if (this.armorType == CHESTPLATE)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_CHESTPLATE_TEXTURE);
		else if (this.armorType == LEGGINGS)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_LEGGINGS_TEXTURE);
		else if (this.armorType == BOOTS)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_BOOTS_TEXTURE);
	}*/

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
		if (this.armorType == EntityEquipmentSlot.CHEST)
			return true;
		else
			return false;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
				true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return 5;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return 1000000;
	}

}

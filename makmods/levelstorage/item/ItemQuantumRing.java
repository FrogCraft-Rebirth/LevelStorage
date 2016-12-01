package makmods.levelstorage.item;

import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuantumRing extends Item implements IElectricItem, IHasRecipe {

	public static final int TIER = 3;
	public static final int STORAGE = 320 * 1000 * 1000;
	// Energy per 1 damage
	public static final int ENERGY_PER_DAMAGE = 400;

	static {
		MinecraftForge.EVENT_BUS.register(ItemQuantumRing.class);
	}

	public ItemQuantumRing(int id) {
		super();

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	@SubscribeEvent
	public static void onDamage(LivingHurtEvent event) {
		// if (event.source.isUnblockable() && event.source ==
		// DamageSource.fall))
		// return;
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			ItemStack ring = getRing(player);
			if (ring != null) {
				int energy = (int) (event.getAmount() * ENERGY_PER_DAMAGE);
				if (ElectricItem.manager.canUse(ring, energy)) {
					ElectricItem.manager.use(ring, energy, player);
					event.setCanceled(true);
				}
			}
		}
	}

	public static ItemStack getRing(EntityPlayer ep) {
		InventoryPlayer inventory = ep.inventory;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack != null)
				if (stack.getItem() instanceof ItemQuantumRing)
					return stack;
		}
		return null;
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
		return 100000;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.QUANTUM_RING_TEXTURE);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\247b"
				+ StatCollector.translateToLocal("tooltip.quantumring"));
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	public boolean chargeItem(ItemStack stack, ItemStack ring) {
		if (stack == null)
			return false;
		if (!(stack.getItem() instanceof IElectricItem))
			return false;
		IElectricItem item = (IElectricItem) stack.getItem();
		if (!ElectricItem.manager.canUse(ring, item.getTransferLimit(stack)))
			return false;
		double chargeSt = ElectricItem.manager.charge(stack,
				item.getTransferLimit(stack), 4, false, false);
		double dischSt = ElectricItem.manager.discharge(ring, chargeSt, 4, true, false, false);
		return chargeSt > 0 && dischSt > 0;
	}

	public void onUpdate(ItemStack itemStack, World world, Entity par3Entity,
			int par4, boolean par5) {
		if (!(par3Entity instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) par3Entity;
		InventoryPlayer inv = player.inventory;
		boolean didSomething = false;
		for (ItemStack device : inv.armorInventory)
			if (chargeItem(device, itemStack)) {
				didSomething = true;
				break;
			}
		if (!didSomething) {
			for (ItemStack device : inv.mainInventory) {
				if (device != null)
					if (device.getItem() instanceof ItemQuantumRing)
						continue;
				if (chargeItem(device, itemStack)) {
					didSomething = true;
					break;
				}
			}
		}
		ArmorFunctions.extinguish(player, world);
		ArmorFunctions.fly(ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK,
				player, itemStack, world);
		ArmorFunctions.helmetFunctions(world, player, itemStack,
				ItemArmorTeslaHelmet.RAY_COST, ItemArmorTeslaHelmet.FOOD_COST);
		ArmorFunctions.jumpBooster(world, player, itemStack);
		ArmorFunctions.speedUp(player, itemStack);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemQuantumRing), "hic", "iei", "bil",
				Character.valueOf('i'), IC2ItemsShortcut.IRIDIUM_PLATE.copy(),
				Character.valueOf('e'), new ItemStack(
						LSBlockItemList.itemStorageFourtyMillion), Character
						.valueOf('h'), new ItemStack(
						LSBlockItemList.itemArmorTeslaHelmet), Character
						.valueOf('c'), new ItemStack(
						LSBlockItemList.itemArmorEnergeticChestplate),
				Character.valueOf('b'), new ItemStack(
						LSBlockItemList.itemLevitationBoots), Character
						.valueOf('l'), new ItemStack(
						LSBlockItemList.itemSupersonicLeggings));

	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab,
			List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
}

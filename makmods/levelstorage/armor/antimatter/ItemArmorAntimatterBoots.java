package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.api.IFlyArmor;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemArmorAntimatterBoots extends ItemArmorAntimatterBase implements
		IFlyArmor, IHasRecipe {
	public ItemArmorAntimatterBoots(int id) {
		super(id, ItemArmorAntimatterBase.BOOTS);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityLivingFallEvent(LivingFallEvent event) {
		if ((!event.getEntityLiving().worldObj.isRemote)
				&& ((event.getEntity() instanceof EntityLivingBase))) {
			EntityLivingBase entity = event.getEntityLiving();
			ItemStack armor = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);

			if ((armor != null) && (armor.getItem() == this)) {
				int fallDamage = Math.max((int) event.getDistance() - 3 - 7, 0);
				int energyCost = ENERGY_PER_DAMAGE * fallDamage;

				if (energyCost <= ElectricItem.manager.getCharge(armor)) {
					ElectricItem.manager.discharge(armor, energyCost,
							2147483647, true, false, false);

					event.setCanceled(true);
				}
			}
		}
	}

	@Override
	public boolean isFlyArmor(ItemStack is) {
		return true;
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemArmorAntimatterBoots), 
				"ici", "iai", "pep", 
				'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(), 
				'e', new ItemStack(LSBlockItemList.itemAntimatterCrystal), 
				'a', new ItemStack(LSBlockItemList.itemLevitationBoots), 
				'c', IC2Items.getItem("static_boots"), 
				'i', IC2ItemsShortcut.IRIDIUM_PLATE);
	}
}

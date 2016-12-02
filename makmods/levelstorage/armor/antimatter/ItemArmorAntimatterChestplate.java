package makmods.levelstorage.armor.antimatter;

import java.util.List;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.CommonHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemArmorAntimatterChestplate extends ItemArmorAntimatterBase
		implements IHasRecipe {

	public static final int INDEX = ItemArmorAntimatterBase.CHESTPLATE + 1;
	public static final int KEEPAWAY_DISTANCE = 16;
	public static final int ENERGY_PER_HIT = 1000;

	public ItemArmorAntimatterChestplate(int id) {
		super(id, ItemArmorAntimatterBase.CHESTPLATE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent event) {
		// if (event.entityLiving.worldObj.isRemote)
		// return;
		if (event.getEntityLiving() instanceof EntityPlayer)
			return;
		if (!(event.getEntityLiving() instanceof EntityMob))
			return;
		List<EntityPlayer> playerList = event.getEntityLiving().worldObj.playerEntities;
		for (EntityPlayer ep : playerList) {
			ItemStack armor = ep.inventory.armorInventory[INDEX];
			if (armor == null)
				continue;
			if (!(armor.getItem() instanceof ItemArmorAntimatterChestplate))
				continue;
			int distance = CommonHelper.getDistance(ep.posX, ep.posY, ep.posZ,
					event.getEntityLiving().posX, event.getEntityLiving().posY,
					event.getEntityLiving().posZ);
			if (distance < KEEPAWAY_DISTANCE) {
				if (ElectricItem.manager.canUse(armor, ENERGY_PER_HIT)) {
					if (!event.getEntityLiving().worldObj.isRemote)
						ElectricItem.manager.use(armor, ENERGY_PER_HIT, ep);
					event.getEntityLiving().attackEntityFrom(
							LSDamageSource.forcefieldArmorInstaKill, 20.0F);
				}
			}
		}
	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		// if (event.entityLiving.worldObj.isRemote)
		// return;
		if (!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer ep = (EntityPlayer) event.getEntityLiving();
		ItemStack armor = ep.inventory.armorInventory[INDEX];
		if (armor == null)
			return;
		if (!(armor.getItem() instanceof ItemArmorAntimatterChestplate))
			return;
		int toBeUsed = (int) (event.getAmount() * ItemArmorAntimatterBase.ENERGY_PER_DAMAGE);
		if (ElectricItem.manager.canUse(armor, toBeUsed)) {
			if (!event.getEntityLiving().worldObj.isRemote)
				ElectricItem.manager.use(armor, toBeUsed, ep);
			event.setCanceled(true);
		}
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.itemArmorAntimatterChestplate), 
				"pcp", "pap", "pep", 
				'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(), 
				'e', new ItemStack(LSBlockItemList.itemAntimatterCrystal), 
				'a', new ItemStack(LSBlockItemList.itemArmorEnergeticChestplate),
				'c', IC2Items.getItem("te", "tesla_coil"));
	}
}

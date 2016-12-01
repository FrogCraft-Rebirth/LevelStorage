package makmods.levelstorage.logic;

import ic2.api.item.ElectricItem;
import makmods.levelstorage.item.ItemPocketRefrigerant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LevelStorageEventHandler {
	@SubscribeEvent
	public void onFireHurt(LivingHurtEvent event) {
		if (event.getSource() == DamageSource.onFire
		        || event.getSource() == DamageSource.lava) {
			if (event.getEntity() instanceof EntityPlayer) {
				for (int i = 0; i < 9; i++) {
					if (event.getEntityLiving() instanceof EntityPlayer) {
						EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
						if (!player.capabilities.isCreativeMode) {
							if (player.inventory.mainInventory[i] != null) {
								if (player.inventory.mainInventory[i].getItem() instanceof ItemPocketRefrigerant) {
									if (ElectricItem.manager
									        .canUse(((EntityPlayer) event.getEntityLiving()).inventory.mainInventory[i],
									                ItemPocketRefrigerant.ENERGY_PER_USE)) {
										ElectricItem.manager
										        .use(((EntityPlayer) event.getEntityLiving()).inventory.mainInventory[i],
										                ItemPocketRefrigerant.ENERGY_PER_USE,
										                event.getEntityLiving());
										((EntityPlayer) event.getEntityLiving())
										        .addPotionEffect(new PotionEffect(
										                Potion.getPotionById(12), 100, 1));
										event.setCanceled(true);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

package makmods.levelstorage.client;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectricHUD {

	// According to https://github.com/MinecraftForge/MinecraftForge/issues/960,
	// ITickHandler is replaced by TickEvent.
	
	public ElectricHUD() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			renderHUD();
	}

	private static class SimpleEItemsData {
		private int capacity;
		private int charge;

		public SimpleEItemsData(int maxCap, int ch) {
			this.capacity = maxCap;
			this.charge = ch;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		public int getCharge() {
			return charge;
		}

		public void setCharge(int charge) {
			this.charge = charge;
		}
	}

	public SimpleEItemsData calculateEItemsData(ItemStack[] inv) {
		int capacity = 0;
		int charged = 0;
		for (ItemStack stack : inv) {
			if (stack == null)
				continue;
			if (stack.getItem() instanceof IElectricItem) {
				IElectricItem electricItem = (IElectricItem) stack.getItem();
				capacity += electricItem.getMaxCharge(stack);
				charged += ElectricItem.manager.getCharge(stack);
			}
		}
		return new SimpleEItemsData(capacity, charged);
	}

	public void renderHUD() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player != null && Minecraft.getMinecraft().currentScreen == null
				&& !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			// ARMOR CALCULATIONS
			int percentArmor;
			TextFormatting color = TextFormatting.WHITE;
			SimpleEItemsData armorData = calculateEItemsData(player.inventory.armorInventory);
			if (armorData.capacity != 0)
				percentArmor = (int) ((armorData.charge * 100.0f) / armorData.capacity);
			else
				percentArmor = 0;

			if (percentArmor > 0 && percentArmor <= 10)
				color = TextFormatting.RED;
			else if (percentArmor > 10 && percentArmor <= 20)
				color = TextFormatting.DARK_RED;
			else if (percentArmor > 20 && percentArmor <= 40)
				color = TextFormatting.GOLD;
			else if (percentArmor > 40 && percentArmor <= 60)
				color = TextFormatting.YELLOW;
			else
				color = TextFormatting.WHITE;

			// ACTUAL RENDERING
			if (percentArmor > 0) {
				FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
				fr.drawStringWithShadow(
						color + I18n.format("other.energyHUD")
								+ " " + percentArmor + "%", 2, 2, 0);
			}
		}
	}

}

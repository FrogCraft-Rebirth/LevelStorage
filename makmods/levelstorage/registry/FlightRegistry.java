package makmods.levelstorage.registry;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.PacketFlightUpdate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class FlightRegistry {

	public static FlightRegistry instance = null;

	public Map<String, Flight> modEnabledFlights = Maps.newHashMap();

	public FlightRegistry() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static class Flight {
		private EntityPlayer player;
		private boolean active;

		public Flight(EntityPlayer player) {
			this.player = player;
			setActive(false);
		}

		public Flight(EntityPlayer player, boolean active) {
			this.player = player;
			setActive(active);
		}

		public EntityPlayer getPlayer() {
			return player;
		}

		public void setPlayer(EntityPlayer player) {
			this.player = player;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.PlayerTickEvent event) {
		if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END)
			return;
		EntityPlayer p = event.player;
		for (Entry<String, Flight> flight : this.modEnabledFlights.entrySet()) {
			if (flight.getKey() == Reference.MOD_ID) {
				if (p != null)
					ItemArmorLevitationBoots.checkPlayer(p);
				if (flight.getValue().active == false) {
					if (p.capabilities.allowFlying || p.capabilities.isFlying) {
						PacketFlightUpdate flUpd = new PacketFlightUpdate();
						flUpd.allowFlying = false;
						flUpd.isFlying = false;
						PacketDispatcher.sendPacketToPlayer(flUpd, p);
					}
				}
			}
		}
	}

}

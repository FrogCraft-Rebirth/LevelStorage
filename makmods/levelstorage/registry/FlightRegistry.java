package makmods.levelstorage.registry;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.network.PacketFlightUpdate;
import makmods.levelstorage.network.packet.PacketTypeHandler;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.ITickHandler;
import net.minecraftforge.fml.common.TickType;
import net.minecraftforge.fml.common.network.PacketDispatcher;
import net.minecraftforge.fml.common.network.Player;
import net.minecraftforge.fml.common.registry.TickRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class FlightRegistry implements ITickHandler {

	public static FlightRegistry instance = null;

	public Map<String, Flight> modEnabledFlights = Maps.newHashMap();

	public FlightRegistry() {
		// TickRegistry.registerTickHandler(this, Side.CLIENT);
		TickRegistry.registerTickHandler(this, Side.SERVER);
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

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// modEnabledFlights.clear();
		;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (tickData[0] == null)
			return;
		if (!(tickData[0] instanceof EntityPlayer))
			return;
		EntityPlayer p = (EntityPlayer) tickData[0];
		for (Entry<String, Flight> flight : this.modEnabledFlights.entrySet()) {
			if (flight.getKey() == Reference.MOD_ID) {
				if (p != null)
					ItemArmorLevitationBoots.checkPlayer(p);
				if (flight.getValue().active == false) {
					if (p.capabilities.allowFlying || p.capabilities.isFlying) {
						PacketFlightUpdate flUpd = new PacketFlightUpdate();
						flUpd.allowFlying = false;
						flUpd.isFlying = false;
						PacketDispatcher
						        .sendPacketToPlayer(
						                PacketTypeHandler.populatePacket(flUpd),
						                (Player) p);
					}
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "FlightRegistryTickHandler";
	}

}

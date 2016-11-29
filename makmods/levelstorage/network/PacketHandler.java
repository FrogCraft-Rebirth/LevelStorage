package makmods.levelstorage.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.network.packet.PacketTypeHandler;
import makmods.levelstorage.proxy.LSKeyboard;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public enum PacketHandler {

	INSTANCE; // Singleton

	private final FMLEventChannel channel;

	PacketHandler() {
		this.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.MOD_ID);
	}

	@SubscribeEvent // There seems to be three different channels. Should merge into one.
	public void onServerPacketData(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if (packet.channel.equals(LSKeyboard.PACKET_KEYBOARD_CHANNEL)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
			DataInputStream dis = new DataInputStream(bis);
			try {
				String keyName = dis.readUTF();
				boolean active = dis.readBoolean();
				LevelStorage.keyboard.handleKeyChangeServer(
						(EntityPlayerMP) player, keyName, active);
				// LevelStorage.keyboard.printKeys();
			} catch (Exception e) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				playerMP.playerNetServerHandler.kickPlayerFromServer("Hacker!");
			}
			return;
		} else if (packet.channel.equals(Reference.CUSTOM_PACKET_CHANNEL)) {
			
		} else if (packet.channel.equals(Reference.MOD_ID)) {
			PacketLS packetEE = PacketTypeHandler.buildPacket(packet.data);
			packetEE.execute(manager, player);
		}
	}

	@SubscribeEvent
	public void onClientPacketData(FMLNetworkEvent.ClientCustomPacketEvent event) {

	}

}

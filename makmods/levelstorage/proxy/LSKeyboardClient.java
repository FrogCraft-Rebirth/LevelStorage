package makmods.levelstorage.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import io.netty.buffer.Unpooled;
import makmods.levelstorage.network.PacketDispatcher;

public class LSKeyboardClient extends LSKeyboard {

	public Map<String, Boolean> keyStatus = Maps.newHashMap();

	public Map<String, KeyBinding> modBindings = Maps.newHashMap();

	public static final String LS_KEY = "level_storage_key";

	public static final KeyBinding RANGE = new KeyBinding("key.range", Keyboard.KEY_V, LS_KEY);
	public static final KeyBinding RAY_SHOOT = new KeyBinding("key.shoot", Keyboard.KEY_R, LS_KEY);
	public static final KeyBinding JETPACK_SWITCH = new KeyBinding("key.jetpackSwitch", Keyboard.KEY_F, LS_KEY);
	public static final KeyBinding ANTIMATTER_BOOTS_FLIGHT = new KeyBinding("key.antimatterBoots", Keyboard.KEY_G, LS_KEY);

	public LSKeyboardClient() {
		// Doing this will let ide be quiet - will be removed soon (tm)
		modBindings.put(RANGE_KEY_NAME, RANGE);
		modBindings.put(RAY_SHOOT_KEY_NAME, RAY_SHOOT);
		modBindings.put(JETPACK_SWITCH_KEY_NAME, JETPACK_SWITCH);
		modBindings.put(ANTIMATTER_BOOTS_SPECIAL_FLIGHT, ANTIMATTER_BOOTS_FLIGHT);
		MinecraftForge.EVENT_BUS.register(this); //TODO: make it client side only
	}

	public void initiateKeyChange(String id, EntityPlayer player,
			boolean isActive) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeUTF(id);
			dos.writeBoolean(isActive);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = bos.toByteArray();/*
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = LSKeyboard.PACKET_KEYBOARD_CHANNEL;
		packet.isChunkDataPacket = false;
		packet.data = data;
		packet.length = data.length;
		PacketDispatcher.sendPacketToServer(packet);*/
		CPacketCustomPayload packet = new CPacketCustomPayload(LSKeyboard.PACKET_KEYBOARD_CHANNEL, new PacketBuffer(Unpooled.wrappedBuffer(data)));
		PacketDispatcher.sendPacketToServer(packet);
	}

	@SubscribeEvent
	public void tickStart(TickEvent.PlayerTickEvent event) {
		if (!(event.phase == TickEvent.Phase.START))
			return;
		EntityPlayer player = event.player;
		for (Entry<String, KeyBinding> entry : modBindings.entrySet()) {
			boolean isActive = entry.getValue().isPressed();
			if (!keyStatus.containsKey(entry.getKey())) {
				keyStatus.put(entry.getKey(), isActive);
				initiateKeyChange(entry.getKey(), player, isActive);
			} else {
				if (keyStatus.get(entry.getKey()) != isActive) {
					keyStatus.remove(entry.getKey());
					keyStatus.put(entry.getKey(), isActive);
					initiateKeyChange(entry.getKey(), player, isActive);
				}
			}
		}
	}

}

package makmods.levelstorage.network;

import io.netty.buffer.Unpooled;
import makmods.levelstorage.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public final class PacketDispatcher {

	private PacketDispatcher() {
	}

	private static FMLProxyPacket getFMLProxyPacket(PacketLS pkt) {
		return new FMLProxyPacket(new PacketBuffer(Unpooled.wrappedBuffer(pkt.populate())), Reference.MOD_ID);
	}

	public static FMLProxyPacket getFMLProxyPacket(CPacketCustomPayload pkt) {
		return new FMLProxyPacket(pkt);
	}

	public static void sendPacketToServer(PacketLS pkt) {
		PacketHandler.INSTANCE.channel.sendToServer(getFMLProxyPacket(pkt));
	}
	
	public static void sendPacketToServer(CPacketCustomPayload pkt) {
		PacketHandler.INSTANCE.channel.sendToServer(getFMLProxyPacket(pkt));
	}

	public static void sendPacketToAll(PacketLS pkt) {
		PacketHandler.INSTANCE.channel.sendToAll(getFMLProxyPacket(pkt));
	}

	public static void sendPacketToPlayer(PacketLS pkt, EntityPlayer p) {
		PacketHandler.INSTANCE.channel.sendTo(getFMLProxyPacket(pkt), (EntityPlayerMP) p);
	}

	public static void sendPacketToAllAround(PacketLS pkt, int dim, BlockPos pos, int range) {
		PacketHandler.INSTANCE.channel.sendToAllAround(getFMLProxyPacket(pkt),
				new TargetPoint(dim, pos.getX(), pos.getY(), pos.getZ(), range));
	}
	
	public static void sendPacketToDim(PacketLS pkt, int dim) {
		PacketHandler.INSTANCE.channel.sendToDimension(getFMLProxyPacket(pkt), dim);
	}
	
	public static void sendPacketToDim(CPacketCustomPayload pkt, int dim) {
		PacketHandler.INSTANCE.channel.sendToDimension(getFMLProxyPacket(pkt), dim);
	}
}

package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.client.render.EnergyRayFX;
import makmods.levelstorage.network.PacketLS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraftforge.fml.common.network.PacketDispatcher;
import net.minecraftforge.fml.common.network.Player;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTeslaRay extends PacketLS {

	public double initX;
	public double initY;
	public double initZ;
	public double tX;
	public double tY;
	public double tZ;

	public PacketTeslaRay() {
		super(PacketTypeHandler.PACKET_TESLA_RAY, false);
	}

	public static void issue(double x, double y, double z, double tX,
			double tY, double tZ) {
		PacketTeslaRay ptr = new PacketTeslaRay();
		ptr.initX = x;
		ptr.initY = y;
		ptr.initZ = z;
		ptr.tX = tX;
		ptr.tY = tY;
		ptr.tZ = tZ;
		PacketDispatcher.sendPacketToAllPlayers(PacketTypeHandler
				.populatePacket(ptr));
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.initX = data.readDouble();
		this.initY = data.readDouble();
		this.initZ = data.readDouble();
		this.tX = data.readDouble();
		this.tY = data.readDouble();
		this.tZ = data.readDouble();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeDouble(initX);
		dos.writeDouble(initY);
		dos.writeDouble(initZ);
		dos.writeDouble(tX);
		dos.writeDouble(tY);
		dos.writeDouble(tZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(INetworkManager network, Player player) {
		if (LevelStorage.getSide().isClient()) {
			EntityPlayer p = (EntityPlayer) player;
			EnergyRayFX pe = new EnergyRayFX(p.worldObj, initX, initY, initZ,
					tX, tY, tZ, 48, 141, 255, 40);
			Minecraft.getMinecraft().effectRenderer.addEffect(pe);
		}
	}

}

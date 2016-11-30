package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.PacketLS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReRender extends PacketLS {
	
	public int x, y, z;

	public PacketReRender() {
		super(PacketTypeHandler.PACKET_RERENDER, false);
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
	}
	
	public static void reRenderBlock(int x, int y, int z) {
		PacketReRender prr = new PacketReRender();
		prr.x = x;
		prr.y = y;
		prr.z = z;
		PacketDispatcher.sendPacketToAll(prr);
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(NetworkManager network, EntityPlayer player) {
		Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
	}

}

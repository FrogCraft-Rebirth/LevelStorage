package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.network.PacketLS;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.PacketDispatcher;
import net.minecraftforge.fml.common.network.Player;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTileUpdate extends PacketLS {

	public int x;
	public int y;
	public int z;
	public NBTTagCompound nbt;
	public static final int UPDATE_RANGE = 64;

	public PacketTileUpdate() {
		super(PacketTypeHandler.PACKET_TILE_UPDATE, false);
	}

	public static final void synhronizeTileEntityAt(TileEntity te) {
		PacketTileUpdate ptu = new PacketTileUpdate();
		ptu.x = te.xCoord;
		ptu.y = te.yCoord;
		ptu.z = te.zCoord;
		te.writeToNBT(ptu.nbt);
		PacketDispatcher.sendPacketToAllAround(te.xCoord, te.yCoord, te.zCoord,
				UPDATE_RANGE, te.worldObj.provider.dimensionId,
				PacketTypeHandler.populatePacket(ptu));
	}

	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		int length = data.readInt();
		byte[] compressed = new byte[length];
		data.readFully(compressed);
		nbt = CompressedStreamTools.decompress(compressed);
	}

	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		byte[] data = CompressedStreamTools.compress(nbt);
		dos.writeInt(data.length);
		dos.write(data);
	}

	@SideOnly(Side.CLIENT)
	public void execute(INetworkManager network, Player player) {
		try {
			Minecraft.getMinecraft().theWorld.getBlockTileEntity(x, y, z)
					.readFromNBT(nbt);
		} catch (Throwable t) {
			LogHelper
					.severe("Exception trying to synchronize TileEntity. Game should have crashed now, but I canceled it via allmighty try {} catch {} block");
			t.printStackTrace();
		}
	}

}

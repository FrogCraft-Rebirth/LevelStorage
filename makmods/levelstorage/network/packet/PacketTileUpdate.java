package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.PacketLS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
		ptu.x = te.getPos().getX();
		ptu.y = te.getPos().getY();
		ptu.z = te.getPos().getZ();
		te.writeToNBT(ptu.nbt);
		PacketDispatcher.sendPacketToAllAround(ptu, te.getWorld().provider.getDimension(), te.getPos(), UPDATE_RANGE);
	}

	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		nbt = CompressedStreamTools.readCompressed(data);
	}

	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		CompressedStreamTools.writeCompressed(nbt, dos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(NetworkManager network, EntityPlayer player) {
		try {
			Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z)).readFromNBT(nbt);
		} catch (Throwable t) {
			LogHelper.severe("Exception trying to synchronize TileEntity. Game should have crashed now, but I canceled it via allmighty try {} catch {} block");
			t.printStackTrace();
		}
	}

}

package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.PacketLS;
import makmods.levelstorage.tileentity.template.IHasButtons;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class PacketPressButton extends PacketLS {

	public int buttonId;
	public int x;
	public int y;
	public int z;
	public int dimId;

	public PacketPressButton() {
		super(PacketTypeHandler.PACKET_PRESS_BUTTON, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.buttonId = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.dimId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.buttonId);
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
		dos.writeInt(this.dimId);
	}
	
	public static void handleButtonClick(int buttonID, TileEntity tileEntity) {
		PacketPressButton packet = new PacketPressButton();
		packet.buttonId = buttonID;
		packet.x = tileEntity.getPos().getX();
		packet.y = tileEntity.getPos().getY();
		packet.z = tileEntity.getPos().getZ();
		packet.dimId = tileEntity.getWorld().provider.getDimension();
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	public void execute(NetworkManager network, EntityPlayer player) {
		try {
			WorldServer world = DimensionManager.getWorld(this.dimId);
			if (world != null) {
				TileEntity te = world.getTileEntity(new BlockPos(this.x, this.y, this.z));
				if (te != null) {
					if (te instanceof IHasButtons) {
						IHasButtons ihb = (IHasButtons) te;
						ihb.handleButtonClick(this.buttonId);
					}
				}
			}
		} catch (Exception e) {
			LogHelper.severe("PacketPressButton - exception:");
			e.printStackTrace();
		}
	}

}

package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.network.PacketLS;
import makmods.levelstorage.tileentity.template.IHasTextBoxes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class PacketTextChanged extends PacketLS {

	public int textBoxId;
	public int x;
	public int y;
	public int z;
	public int dimId;
	public String newText;

	public PacketTextChanged() {
		super(PacketTypeHandler.PACKET_TEXT_CHANGED, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.textBoxId = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.dimId = data.readInt();
		this.newText = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.textBoxId);
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
		dos.writeInt(this.dimId);
		dos.writeUTF(this.newText);
	}

	@Override
	public void execute(NetworkManager network, EntityPlayer player) {
		try {
			WorldServer world = DimensionManager.getWorld(this.dimId);

			if (world != null) {
				if (!world.isRemote) {
					TileEntity te = world.getTileEntity(new BlockPos(this.x, this.y,
					        this.z));
					if (te != null) {
						if (te instanceof IHasTextBoxes) {
							IHasTextBoxes ihb = (IHasTextBoxes) te;
							ihb.handleTextChange(this.newText);
						}
					}
				}
			}
		} catch (Exception e) {
			LogHelper.severe("LevelStorage: PacketPressButton - exception:");
			e.printStackTrace();
		}
	}

}

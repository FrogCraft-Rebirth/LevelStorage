package makmods.levelstorage.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.network.packet.PacketTypeHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

/**
 * @author pahimar
 * 
 */
public class PacketLS {

	public PacketTypeHandler packetType;
	public boolean isChunkDataPacket;

	public PacketLS(PacketTypeHandler packetType, boolean isChunkDataPacket) {

		this.packetType = packetType;
		this.isChunkDataPacket = isChunkDataPacket;
	}

	public byte[] populate() {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try {
			dos.writeByte(this.packetType.ordinal());
			this.writeData(dos);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		return bos.toByteArray();
	}

	public void readPopulate(DataInputStream data) {

		try {
			this.readData(data);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void readData(DataInputStream data) throws IOException {

	}

	public void writeData(DataOutputStream dos) throws IOException {

	}
/*
	public void execute(INetworkManager network, Player player) {

	}*/
	
	//One can only hope this is correct replacement
	public void execute(NetworkManager network, EntityPlayer player) {
		
	}

	public void setKey(int key) {

	}
}

package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.network.PacketLS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticle extends PacketLS {

	public double x;
	public double y;
	public double z;
	public double velX;
	public double velY;
	public double velZ;
	public String name;

	public PacketParticle() {
		super(PacketTypeHandler.PACKET_PARTICLE, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.velX = data.readDouble();
		this.velY = data.readDouble();
		this.velZ = data.readDouble();
		this.x = data.readDouble();
		this.y = data.readDouble();
		this.z = data.readDouble();
		this.name = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeDouble(this.velX);
		dos.writeDouble(this.velY);
		dos.writeDouble(this.velZ);
		dos.writeDouble(this.x);
		dos.writeDouble(this.y);
		dos.writeDouble(this.z);
		dos.writeUTF(this.name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(NetworkManager network, EntityPlayer player) {
		player.worldObj.spawnParticle(this.name, this.x, this.y, this.z, this.velX, this.velY, this.velZ);
		//No way to add custom particle???
		//player.worldObj.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.x, this.y, this.z, this.velX, this.velY, this.velZ);
		
	}

}

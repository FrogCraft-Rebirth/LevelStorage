package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import makmods.levelstorage.network.PacketLS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticles extends PacketLS {

	public List<ParticleInternal> particles = Lists.newArrayList();

	public static class ParticleInternal {
		public double x;
		public double y;
		public double z;
		public double velX;
		public double velY;
		public double velZ;
		public String name;
	}

	public PacketParticles() {
		super(PacketTypeHandler.PACKET_PARTICLES, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		try {
			int size = data.readInt();
			for (int i = 0; i < size; i++) {
				ParticleInternal pi = new ParticleInternal();
				pi.velX = data.readDouble();
				pi.velY = data.readDouble();
				pi.velZ = data.readDouble();
				pi.x = data.readDouble();
				pi.y = data.readDouble();
				pi.z = data.readDouble();
				pi.name = data.readUTF();
				particles.add(pi);
			}
		} catch (Throwable t) {
		}
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		try {
			int size = particles.size();
			dos.writeInt(size);
			for (int i = 0; i < size; i++) {
				ParticleInternal pi = particles.get(i);
				dos.writeDouble(pi.velX);
				dos.writeDouble(pi.velY);
				dos.writeDouble(pi.velZ);
				dos.writeDouble(pi.x);
				dos.writeDouble(pi.y);
				dos.writeDouble(pi.z);
				dos.writeUTF(pi.name);
			}
		} catch (Throwable t) {
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(NetworkManager network, EntityPlayer player) {
		EntityPlayer p = (EntityPlayer) player;
		for (ParticleInternal pi : particles) {
			p.worldObj.spawnParticle(pi.name, pi.x, pi.y, pi.z, pi.velX, pi.velY, pi.velZ);
		}
	}

}

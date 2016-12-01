package makmods.levelstorage.dimension;

import java.util.Random;

import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.packet.PacketTeslaRay;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntimatterUniverseRayHandler {

	public static final int MAX_STARTPOINT_DEVIATION = 256;

	public AntimatterUniverseRayHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onFall(LivingHurtEvent event) {
		try {
			if (event.getSource().equals(DamageSource.fall)
					&& event.getEntityLiving().worldObj.provider.getDimension() == LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID)
				event.setCanceled(true);
		} catch (Exception e) {
		}

	}

	public void spawnDirtyTricks(World world) {
		Random rng = world.rand;
		if (rng.nextInt(10) == 0) {
		int whereToSpawn = rng.nextInt(4);
		int coordinateX = 0;
		int coordinateY = 0;
		int coordinateZ = 0;

		int initX = 0;
		int initY = 256;
		int initZ = 0;

		switch (whereToSpawn) {
		case 0: {
			coordinateX += rng.nextInt(64);
			coordinateZ -= rng.nextInt(64);

			initX -= rng.nextInt(64 / 4);
			initZ += rng.nextInt(64 / 4);
			break;
		}
		case 1: {
			coordinateX -= rng.nextInt(64);
			coordinateZ += rng.nextInt(64);

			initX += rng.nextInt(64 / 4);
			initZ -= rng.nextInt(64 / 4);
			break;
		}
		case 2: {
			coordinateX += rng.nextInt(64);
			coordinateZ += rng.nextInt(64);

			initX -= rng.nextInt(64 / 4);
			initZ -= rng.nextInt(64 / 4);
			break;
		}
		default: {
			coordinateX -= rng.nextInt(64);
			coordinateZ -= rng.nextInt(64);

			initX += rng.nextInt(64 / 4);
			initZ += rng.nextInt(64 / 4);
			break;
		}
		}
		for (int i = 256; i >= 0; i--) {
			if (!world.isAirBlock(new BlockPos(coordinateX, i, coordinateZ))) {
				coordinateY = i;
				break;
			}
		}

		PacketTeslaRay ptr = new PacketTeslaRay();
		ptr.initX = initX;
		ptr.initY = initY;
		ptr.initZ = initZ;
		ptr.tX = coordinateX;
		ptr.tY = coordinateY;
		ptr.tZ = coordinateZ;
		PacketDispatcher.sendPacketToDim(
				/*PacketTypeHandler.populatePacket(ptr)*/ptr,
				world.provider.getDimension());
		CommonHelper.spawnLightning(world, coordinateX, coordinateY,
				coordinateZ, false);
		 }
	}

	public void tickStart(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;
		World w = event.world;
		if (w.provider instanceof WorldProviderAntimatterUniverse) {
			if (w.playerEntities.size() > 0) {
				spawnDirtyTricks(w);
				for (Object o : w.playerEntities) {
					if (!(o instanceof Entity))
						continue;
					Entity e = (Entity) o;
					if (w.rand.nextInt(30) == 0) {

						e.fallDistance = 0.0F;
						if (!e.onGround)
						continue;
						w.createExplosion(null, e.posX, e.posY, e.posZ, 10.0F,
								true);
						e.motionY *= 1.75F;
					}
				}
			}
		}
	}
/*
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Antimatter Universe Dirty Tricks Handler";
	}*/

}

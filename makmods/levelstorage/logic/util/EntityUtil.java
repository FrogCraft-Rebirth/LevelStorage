package makmods.levelstorage.logic.util;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUtil {

	public static Entity getTarget(World world,
			EntityPlayer entityplayer, double range) {
		Entity pointedEntity = null;
		double d = range;
		Vec3d vec3d = new Vec3d(entityplayer.posX,
				entityplayer.posY + entityplayer.getEyeHeight(),
				entityplayer.posZ);
		Vec3d vec3d1 = entityplayer.getLookVec();
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * d, vec3d1.yCoord * d,
				vec3d1.zCoord * d);
		float f1 = 1.1F;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(
				entityplayer,
				entityplayer.getCollisionBoundingBox().addCoord(
						vec3d1.xCoord * d, vec3d1.yCoord * d, 
						vec3d1.zCoord * d).expand(f1, f1, f1));

		double d2 = 0.0D;
		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);
			if ((entity.canBeCollidedWith())
					&& (world.rayTraceBlocks(new Vec3d(entityplayer.posX,
							entityplayer.posY + entityplayer.getEyeHeight(),
							entityplayer.posZ),
							new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(),
								entity.posZ), false, true, false) == null)) {
				float f2 = Math.max(0.8F, entity.getCollisionBorderSize());
				AxisAlignedBB axisalignedbb = entity.getCollisionBoundingBox()
						.expand(f2, f2, f2);
				RayTraceResult movingobjectposition = axisalignedbb
						.calculateIntercept(vec3d, vec3d2);
				if (axisalignedbb.isVecInside(vec3d)) {
					if ((0.0D < d2) || (d2 == 0.0D)) {
						pointedEntity = entity;
						d2 = 0.0D;
					}

				} else if (movingobjectposition != null) {
					double d3 = vec3d.distanceTo(movingobjectposition.hitVec);
					if ((d3 < d2) || (d2 == 0.0D)) {
						pointedEntity = entity;
						d2 = d3;
					}
				}
			}
		}
		return pointedEntity;
	}

}

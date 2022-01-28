package meldexun.reachfix.asm.hook.client;

import java.util.List;

import javax.annotation.Nullable;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

public class EntityRendererHook {

	public static void getMouseOver(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewEntity = mc.getRenderViewEntity();

		if (viewEntity == null) {
			return;
		}
		if (mc.world == null) {
			return;
		}

		mc.profiler.startSection("pick");
		RayTraceResult pointedBlock = getPointedBlock(partialTicks);
		RayTraceResult pointedEntity = getPointedEntity(partialTicks);

		if (pointedBlock != null) {
			if (pointedEntity != null) {
				Vec3d start = viewEntity.getPositionEyes(partialTicks);
				double d1 = pointedBlock.hitVec.squareDistanceTo(start);
				double d2 = pointedEntity.hitVec.squareDistanceTo(start);
				mc.objectMouseOver = d1 <= d2 ? pointedBlock : pointedEntity;
			} else {
				mc.objectMouseOver = pointedBlock;
			}
		} else if (pointedEntity != null) {
			mc.objectMouseOver = pointedEntity;
		} else {
			double reach = mc.playerController.getBlockReachDistance();
			Vec3d dir = viewEntity.getLook(partialTicks).scale(reach);
			Vec3d start = viewEntity.getPositionEyes(partialTicks);
			Vec3d end = start.add(dir);
			mc.objectMouseOver = new RayTraceResult(Type.MISS, end, null, new BlockPos(end));
		}

		mc.entityRenderer.pointedEntity = mc.objectMouseOver.entityHit;
		mc.profiler.endSection();
	}

	@Nullable
	private static RayTraceResult getPointedBlock(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewEntity = mc.getRenderViewEntity();

		if (viewEntity == null) {
			return null;
		}
		if (mc.world == null) {
			return null;
		}

		double reach = ReachFixUtil.getBlockReach(mc.player);
		RayTraceResult result = viewEntity.rayTrace(reach, partialTicks);

		if (result != null && result.typeOfHit == Type.MISS) {
			return null;
		}

		return result;
	}

	@Nullable
	private static RayTraceResult getPointedEntity(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewEntity = mc.getRenderViewEntity();

		if (viewEntity == null) {
			return null;
		}
		if (mc.world == null) {
			return null;
		}

		double reach = ReachFixUtil.getEntityReach(mc.player);
		Vec3d dir = viewEntity.getLook(partialTicks).scale(reach);
		AxisAlignedBB aabb = viewEntity.getEntityBoundingBox();
		aabb = aabb.expand(dir.x, dir.y, dir.z);
		aabb = aabb.grow(1.0D);
		List<Entity> possibleEntities = mc.world.getEntitiesInAABBexcluding(viewEntity, aabb, entity -> {
			if (!EntitySelectors.NOT_SPECTATING.apply(entity)) {
				return false;
			}
			return entity.canBeCollidedWith();
		});

		Vec3d start = viewEntity.getPositionEyes(partialTicks);
		Vec3d end = start.add(dir);
		RayTraceResult result = null;
		Entity pointedEntity = null;
		double min = Double.MAX_VALUE;
		for (Entity entity : possibleEntities) {
			if (viewEntity.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity.canRiderInteract()) {
				continue;
			}

			AxisAlignedBB entityAabb = getInterpolatedAABB(entity, partialTicks);
			if (entityAabb.contains(start)) {
				return new RayTraceResult(entity, start);
			}

			RayTraceResult rtr = entityAabb.calculateIntercept(start, end);
			if (rtr == null || rtr.typeOfHit == Type.MISS) {
				continue;
			}

			double dist = start.squareDistanceTo(rtr.hitVec);
			if (dist < min) {
				result = rtr;
				pointedEntity = entity;
				min = dist;
			}
		}

		if (result == null || result.typeOfHit == Type.MISS) {
			return null;
		}

		return new RayTraceResult(pointedEntity, result.hitVec);
	}

	private static AxisAlignedBB getInterpolatedAABB(Entity entity, float partialTicks) {
		AxisAlignedBB aabb = entity.getEntityBoundingBox();
		float collisionBorderSize = entity.getCollisionBorderSize();
		if (collisionBorderSize != 0.0F) {
			aabb = aabb.grow(collisionBorderSize);
		}
		if (partialTicks != 0.0F) {
			double x = MathHelper.clampedLerp(0.0D, entity.posX - entity.prevPosX, partialTicks);
			double y = MathHelper.clampedLerp(0.0D, entity.posY - entity.prevPosY, partialTicks);
			double z = MathHelper.clampedLerp(0.0D, entity.posZ - entity.prevPosZ, partialTicks);
			aabb = aabb.offset(x, y, z);
		}
		return aabb;
	}
}

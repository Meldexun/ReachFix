package meldexun.reachfix.hook.client;

import java.util.List;

import javax.annotation.Nullable;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRendererHook {

	public static void getMouseOver(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewEntity = mc.getRenderViewEntity();

		if (viewEntity == null) {
			return;
		}
		if (mc.player == null) {
			return;
		}
		if (mc.world == null) {
			return;
		}

		mc.profiler.startSection("pick");
		mc.objectMouseOver = pointedObject(viewEntity, mc.player, EnumHand.MAIN_HAND, mc.world, partialTicks);
		mc.entityRenderer.pointedEntity = mc.objectMouseOver.entityHit;
		mc.pointedEntity = mc.objectMouseOver.entityHit;
		mc.profiler.endSection();
	}

	public static RayTraceResult pointedObject(Entity viewEntity, EntityPlayer player, EnumHand hand, World world, float partialTicks) {
		Vec3d start = viewEntity.getPositionEyes(partialTicks);
		Vec3d look = viewEntity.getLook(partialTicks);
		Vec3d endBlock = start.add(look.scale(ReachFixUtil.getBlockReach(player, hand)));
		RayTraceResult pointedBlock = world.rayTraceBlocks(start, endBlock, false, false, false);
		Vec3d endEntity = start.add(look.scale(ReachFixUtil.getEntityReach(player, hand)));
		RayTraceResult pointedEntity = getPointedEntity(viewEntity, world, start, endEntity, partialTicks);

		if (pointedBlock != null) {
			if (pointedEntity != null) {
				double distBlock = start.squareDistanceTo(pointedBlock.hitVec);
				double distEntity = start.squareDistanceTo(pointedEntity.hitVec);
				if (distBlock < distEntity) {
					return pointedBlock;
				} else {
					return pointedEntity;
				}
			} else {
				return pointedBlock;
			}
		} else if (pointedEntity != null) {
			return pointedEntity;
		}

		return new RayTraceResult(Type.MISS, endBlock, null, new BlockPos(endBlock));
	}

	@Nullable
	private static RayTraceResult getPointedEntity(Entity viewEntity, World world, Vec3d start, Vec3d end, float partialTicks) {
		AxisAlignedBB aabb = new AxisAlignedBB(start, end).grow(1.0D);
		Entity lowestRidingEntity = viewEntity.getLowestRidingEntity();
		List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(viewEntity, aabb, entity -> {
			if (!EntitySelectors.NOT_SPECTATING.apply(entity)) {
				return false;
			}
			if (!entity.canBeCollidedWith()) {
				return false;
			}
			if (lowestRidingEntity != entity.getLowestRidingEntity()) {
				return true;
			}
			return entity.canRiderInteract();
		});

		RayTraceResult result = null;
		Entity pointedEntity = null;
		double min = Double.MAX_VALUE;
		for (Entity entity : possibleEntities) {
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
			double x = -(entity.posX - entity.lastTickPosX) * (1.0D - partialTicks);
			double y = -(entity.posY - entity.lastTickPosY) * (1.0D - partialTicks);
			double z = -(entity.posZ - entity.lastTickPosZ) * (1.0D - partialTicks);
			aabb = aabb.offset(x, y, z);
		}
		return aabb;
	}
}

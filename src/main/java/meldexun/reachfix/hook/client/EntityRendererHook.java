package meldexun.reachfix.hook.client;

import java.util.List;

import javax.annotation.Nullable;

import meldexun.reachfix.util.BoundingBoxUtil;
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
		double blockReach = ReachFixUtil.getBlockReach(player, hand);
		double entityReach = ReachFixUtil.getEntityReach(player, hand);
		Vec3d end = start.add(look.scale(Math.max(blockReach, entityReach)));
		RayTraceResult pointedBlock = world.rayTraceBlocks(start, end, false, false, false);
		RayTraceResult pointedEntity = getPointedEntity(viewEntity, world, start, end, partialTicks);

		if (!isNullOrMiss(pointedBlock)) {
			if (!isNullOrMiss(pointedEntity)) {
				double distBlock = start.squareDistanceTo(pointedBlock.hitVec);
				double distEntity = start.squareDistanceTo(pointedEntity.hitVec);
				if (distBlock < distEntity) {
					if (distBlock < blockReach * blockReach) {
						return pointedBlock;
					}
				} else if (distEntity < entityReach * entityReach) {
					return pointedEntity;
				}
			} else if (start.squareDistanceTo(pointedBlock.hitVec) < blockReach * blockReach) {
				return pointedBlock;
			}
		} else if (!isNullOrMiss(pointedEntity) && start.squareDistanceTo(pointedEntity.hitVec) < entityReach * entityReach) {
			return pointedEntity;
		}

		return new RayTraceResult(Type.MISS, end, null, new BlockPos(end));
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
			AxisAlignedBB entityAabb = BoundingBoxUtil.getInteractionBoundingBox(entity, partialTicks);
			if (entityAabb.contains(start)) {
				return new RayTraceResult(entity, start);
			}

			RayTraceResult rtr = entityAabb.calculateIntercept(start, end);
			if (isNullOrMiss(rtr)) {
				continue;
			}

			double dist = start.squareDistanceTo(rtr.hitVec);
			if (dist < min) {
				result = rtr;
				pointedEntity = entity;
				min = dist;
			}
		}

		if (isNullOrMiss(result)) {
			return null;
		}

		return new RayTraceResult(pointedEntity, result.hitVec);
	}

	private static boolean isNullOrMiss(RayTraceResult rayTraceResult) {
		return rayTraceResult == null || rayTraceResult.typeOfHit == Type.MISS;
	}

}

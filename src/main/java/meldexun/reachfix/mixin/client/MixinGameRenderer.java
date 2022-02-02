package meldexun.reachfix.mixin.client;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(method = "pick", cancellable = true, at = @At("HEAD"))
	public void onPick(float partialTicks, CallbackInfo info) {
		info.cancel();

		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();

		if (viewEntity == null) {
			return;
		}
		if (mc.level == null) {
			return;
		}

        mc.getProfiler().push("pick");
		RayTraceResult pointedBlock = getPointedBlock(partialTicks);
		RayTraceResult pointedEntity = getPointedEntity(partialTicks);

		if (pointedBlock != null) {
			if (pointedEntity != null) {
				Vector3d start = viewEntity.getEyePosition(partialTicks);
				double d1 = pointedBlock.getLocation().distanceToSqr(start);
				double d2 = pointedEntity.getLocation().distanceToSqr(start);
				mc.hitResult = d1 <= d2 ? pointedBlock : pointedEntity;
			} else {
				mc.hitResult = pointedBlock;
			}
		} else if (pointedEntity != null) {
			mc.hitResult = pointedEntity;
		} else {
			double reach = ReachFixUtil.getBlockReach(mc.player);
			Vector3d dir = viewEntity.getViewVector(partialTicks).scale(reach);
			Vector3d start = viewEntity.getEyePosition(partialTicks);
			Vector3d end = start.add(dir);
			mc.hitResult = BlockRayTraceResult.miss(end, null, new BlockPos(end));
		}

        mc.getProfiler().pop();
	}

	@Nullable
	private static RayTraceResult getPointedBlock(float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();

		if (viewEntity == null) {
			return null;
		}
		if (mc.level == null) {
			return null;
		}

		double reach = ReachFixUtil.getBlockReach(mc.player);
		RayTraceResult result = viewEntity.pick(reach, partialTicks, false);

		if (result != null && result.getType() == Type.MISS) {
			return null;
		}

		return result;
	}

	@Nullable
	private static RayTraceResult getPointedEntity(float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();

		if (viewEntity == null) {
			return null;
		}
		if (mc.level == null) {
			return null;
		}

		double reach = ReachFixUtil.getEntityReach(mc.player);
		Vector3d dir = viewEntity.getViewVector(partialTicks).scale(reach);
		AxisAlignedBB aabb = viewEntity.getBoundingBox();
		aabb = aabb.expandTowards(dir.x, dir.y, dir.z);
		aabb = aabb.inflate(1.0D);
		List<Entity> possibleEntities = mc.level.getEntities(viewEntity, aabb, entity -> {
			return !entity.isSpectator() && entity.isPickable();
		});

		Vector3d start = viewEntity.getEyePosition(partialTicks);
		Vector3d end = start.add(dir);
		Vector3d result = null;
		Entity pointedEntity = null;
		double min = Double.MAX_VALUE;
		for (Entity entity : possibleEntities) {
			if (viewEntity.getRootVehicle() == entity.getRootVehicle() && !entity.canRiderInteract()) {
				continue;
			}

			AxisAlignedBB entityAabb = getInterpolatedAABB(entity, partialTicks);
			if (entityAabb.contains(start)) {
				return new EntityRayTraceResult(entity, start);
			}

			Optional<Vector3d> rtr = entityAabb.clip(start, end);
			if (!rtr.isPresent()) {
				continue;
			}

			double dist = start.distanceToSqr(rtr.get());
			if (dist < min) {
				result = rtr.get();
				pointedEntity = entity;
				min = dist;
			}
		}

		return new EntityRayTraceResult(pointedEntity, result);
	}

	private static AxisAlignedBB getInterpolatedAABB(Entity entity, float partialTicks) {
		AxisAlignedBB aabb = entity.getBoundingBox();
		float collisionBorderSize = entity.getPickRadius();
		if (collisionBorderSize != 0.0F) {
			aabb = aabb.inflate(collisionBorderSize);
		}
		if (partialTicks != 0.0F) {
			double x = MathHelper.clampedLerp(0.0D, entity.getX() - entity.xo, partialTicks);
			double y = MathHelper.clampedLerp(0.0D, entity.getY() - entity.yo, partialTicks);
			double z = MathHelper.clampedLerp(0.0D, entity.getZ() - entity.zo, partialTicks);
			aabb = aabb.move(x, y, z);
		}
		return aabb;
	}

}

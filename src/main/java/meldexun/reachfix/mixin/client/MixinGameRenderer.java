package meldexun.reachfix.mixin.client;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(method = "pick", cancellable = true, at = @At("HEAD"))
	public void onPick(float partialTicks, CallbackInfo info) {
		info.cancel();

		Minecraft mc = Minecraft.getInstance();
		Entity cameraEntity = mc.getCameraEntity();

		if (cameraEntity == null) {
			return;
		}
		if (mc.player == null) {
			return;
		}
		if (mc.level == null) {
			return;
		}

		mc.getProfiler().push("pick");
		mc.hitResult = pointedObject(cameraEntity, mc.player, mc.level, partialTicks);
		mc.getProfiler().pop();
	}

	@Unique
	private static RayTraceResult pointedObject(Entity cameraEntity, PlayerEntity player, World level, float partialTicks) {
		Vector3d start = cameraEntity.getEyePosition(partialTicks);
		Vector3d look = cameraEntity.getViewVector(partialTicks);
		Vector3d end = start.add(look.scale(ReachFixUtil.getBlockReach(player)));
		RayTraceResult pointedBlock = level.clip(new RayTraceContext(start, end, BlockMode.OUTLINE, FluidMode.NONE, cameraEntity));

		if (pointedBlock != null) {
			RayTraceResult pointedEntity = getPointedEntity(cameraEntity, level, start, pointedBlock.getLocation(), partialTicks);
			if (pointedEntity == null) {
				return pointedBlock;
			}
			if (start.distanceTo(pointedEntity.getLocation()) <= ReachFixUtil.getEntityReach(player)) {
				return pointedEntity;
			}
		} else {
			Vector3d end1 = start.add(look.scale(ReachFixUtil.getEntityReach(player)));
			RayTraceResult pointedEntity = getPointedEntity(cameraEntity, level, start, end1, partialTicks);
			if (pointedEntity != null) {
				return pointedEntity;
			}
		}

		return BlockRayTraceResult.miss(end, null, new BlockPos(end));
	}

	@Unique
	@Nullable
	private static RayTraceResult getPointedEntity(Entity cameraEntity, World level, Vector3d start, Vector3d end, float partialTicks) {
		AxisAlignedBB aabb = new AxisAlignedBB(start, end).inflate(1.0D);
		Entity rootVehicle = cameraEntity.getRootVehicle();
		List<Entity> possibleEntities = level.getEntities(cameraEntity, aabb, entity -> {
			return !entity.isSpectator() && entity.isPickable() && (rootVehicle != entity.getRootVehicle() || entity.canRiderInteract());
		});

		Vector3d result = null;
		Entity pointedEntity = null;
		double min = Double.MAX_VALUE;
		for (Entity entity : possibleEntities) {
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

		if (pointedEntity == null) {
			return null;
		}

		return new EntityRayTraceResult(pointedEntity, result);
	}

	@Unique
	private static AxisAlignedBB getInterpolatedAABB(Entity entity, float partialTicks) {
		AxisAlignedBB aabb = entity.getBoundingBox();
		float pickRadius = entity.getPickRadius();
		if (pickRadius != 0.0F) {
			aabb = aabb.inflate(pickRadius);
		}
		if (partialTicks != 0.0F) {
			double x = -(entity.getX() - entity.xo) * (1.0D - partialTicks);
			double y = -(entity.getY() - entity.yo) * (1.0D - partialTicks);
			double z = -(entity.getZ() - entity.zo) * (1.0D - partialTicks);
			aabb = aabb.move(x, y, z);
		}
		return aabb;
	}

}

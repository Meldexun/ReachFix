package meldexun.reachfix.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(ServerPlayNetHandler.class)
public class MixinServerPlayNetHandler {

	@Shadow
	private ServerPlayerEntity player;
	@Unique
	private double aabbRadius;

	@Redirect(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;distanceToSqr(Lnet/minecraft/entity/Entity;)D"))
	public double distanceTo(ServerPlayerEntity player, Entity target) {
		AxisAlignedBB aabb = target.getBoundingBox();
		float pickRadius = target.getPickRadius();
		if (pickRadius != 0.0F) {
			aabb = aabb.inflate(pickRadius);
		}
		Vector3d center = aabb.getCenter();
		double rx = center.x - aabb.minX;
		double ry = center.y - aabb.minY;
		double rz = center.z - aabb.minZ;
		this.aabbRadius = Math.sqrt(rx * rx + ry * ry + rz * rz);
		double dx = center.x - player.getX();
		double dy = center.y - player.getEyeY();
		double dz = center.z - player.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@ModifyConstant(method = "handleInteract", constant = @Constant(doubleValue = 36.0D, ordinal = 1))
	public double reachDist(double d) {
		return ReachFixUtil.getEntityReach(this.player) + this.aabbRadius + 1.0D;
	}

}

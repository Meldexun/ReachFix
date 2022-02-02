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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.math.AxisAlignedBB;

@Mixin(ServerPlayNetHandler.class)
public class MixinServerPlayNetHandler {

	@Shadow
	private PlayerEntity player;
	@Unique
	private double aabbRadius;

	@Redirect(method = "handleInteract", at = @At("distanceToSqr"))
	public double distanceTo(PlayerEntity player, Entity target) {
		AxisAlignedBB aabb = target.getBoundingBox();
		float collisionBorderSize = target.getPickRadius();
		if (collisionBorderSize != 0.0F) {
			aabb = aabb.inflate(collisionBorderSize);
		}
		double x = (aabb.maxX - aabb.minX) * 0.5D;
		double y = (aabb.maxY - aabb.minY) * 0.5D;
		double z = (aabb.maxZ - aabb.minZ) * 0.5D;
		this.aabbRadius = Math.sqrt(x * x + y * y + z * z);
		double x1 = aabb.minX + x - player.getX();
		double y1 = aabb.minY + y - (player.getY() + player.getEyeHeight());
		double z1 = aabb.minZ + z - player.getZ();
		return Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
	}

	@ModifyConstant(method = "handleInteract", constant = @Constant(doubleValue = 36.0D, ordinal = 1))
	public double reachDist(double d) {
		return ReachFixUtil.getEntityReach(this.player) + this.aabbRadius + 1.0D;
	}

}

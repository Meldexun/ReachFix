package meldexun.reachfix.hook;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NetHandlerPlayServerHook {

	public static boolean isEntityInRange(NetHandlerPlayServer serverHandler, Entity entity, EnumHand hand) {
		EntityPlayer player = serverHandler.player;
		AxisAlignedBB aabb = entity.getEntityBoundingBox();
		if (entity.getCollisionBorderSize() != 0.0F) {
			aabb = aabb.grow(entity.getCollisionBorderSize());
		}
		double distanceSq = distanceSq(player.getPositionEyes(1.0F), aabb);
		double reach = ReachFixUtil.getEntityReach(player, hand) + 1.0D;
		return distanceSq < reach * reach;
	}

	private static double distanceSq(Vec3d vec, AxisAlignedBB aabb) {
		double dx = MathHelper.clamp(vec.x, aabb.minX, aabb.maxX) - vec.x;
		double dy = MathHelper.clamp(vec.y, aabb.minY, aabb.maxY) - vec.y;
		double dz = MathHelper.clamp(vec.z, aabb.minZ, aabb.maxZ) - vec.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public static double getEyeHeightMinusOnePointFive(NetHandlerPlayServer serverHandler) {
		return serverHandler.player.eyeHeight - 1.5D;
	}

}

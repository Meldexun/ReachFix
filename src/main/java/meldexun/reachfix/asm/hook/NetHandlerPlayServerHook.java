package meldexun.reachfix.asm.hook;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.AxisAlignedBB;

public class NetHandlerPlayServerHook {

	public static boolean isEntityInRange(NetHandlerPlayServer serverHandler, Entity entity) {
		EntityPlayer player = serverHandler.player;
		AxisAlignedBB aabb = entity.getEntityBoundingBox();
		if (entity.getCollisionBorderSize() != 0.0F) {
			aabb = aabb.grow(entity.getCollisionBorderSize());
		}
		double x = (aabb.maxX - aabb.minX) * 0.5D;
		double y = (aabb.maxY - aabb.minY) * 0.5D;
		double z = (aabb.maxZ - aabb.minZ) * 0.5D;
		double aabbRadius = Math.sqrt(x * x + y * y + z * z);
		double x1 = aabb.minX + x - player.posX;
		double y1 = aabb.minY + y - (player.posY + player.eyeHeight);
		double z1 = aabb.minZ + z - player.posZ;
		double distance = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
		double reach = ReachFixUtil.getEntityReach(player);
		return distance < reach + aabbRadius + 1.0D;
	}

	public static double getEyeHeightMinusOnePointFive(NetHandlerPlayServer serverHandler) {
		return serverHandler.player.eyeHeight - 1.5D;
	}

}

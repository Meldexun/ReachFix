package meldexun.reachfix.hook;

import meldexun.reachfix.util.BoundingBoxUtil;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

public class NetHandlerPlayServerHook {

	public static boolean isEntityInRange(NetHandlerPlayServer serverHandler, Entity entity, EnumHand hand) {
		EntityPlayer player = serverHandler.player;
		AxisAlignedBB aabb = BoundingBoxUtil.getInteractionBoundingBox(entity, 1.0F);
		double distanceSq = BoundingBoxUtil.distanceSq(aabb, player.getPositionEyes(1.0F));
		double reach = ReachFixUtil.getEntityReach(player, hand) + 1.0D;
		return distanceSq < reach * reach;
	}

	public static double getEyeHeightMinusOnePointFive(NetHandlerPlayServer serverHandler) {
		return serverHandler.player.eyeHeight - 1.5D;
	}

}

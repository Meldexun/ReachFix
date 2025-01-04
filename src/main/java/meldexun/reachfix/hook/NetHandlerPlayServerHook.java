package meldexun.reachfix.hook;

import lombok.Data;
import meldexun.reachfix.util.BoundingBoxUtil;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@Data
public class NetHandlerPlayServerHook {

    public static boolean isEntityInRange(@NotNull NetHandlerPlayServer serverHandler,
                                          @NotNull Entity entity,
                                          @NotNull EnumHand hand) {
        EntityPlayer player = serverHandler.player;
        @NotNull AxisAlignedBB aabb = BoundingBoxUtil.getInteractionBoundingBox(entity, 1.0F);
        @NotNull Vec3d positionEyes = player.getPositionEyes(1.0F);
        double distanceSq = BoundingBoxUtil.distanceSq(aabb, positionEyes);
        double reach = ReachFixUtil.getEntityReach(player, hand) + 1.0D;
        return distanceSq < reach * reach;
    }

    public static double getEyeHeightMinusOnePointFive(@NotNull NetHandlerPlayServer serverHandler) {
        return serverHandler.player.eyeHeight - 1.5D;
    }
}

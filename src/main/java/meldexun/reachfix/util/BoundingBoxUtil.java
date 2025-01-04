package meldexun.reachfix.util;

import lombok.Data;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@Data
public class BoundingBoxUtil {

    public static @NotNull AxisAlignedBB getInteractionBoundingBox(@NotNull Entity entity, float partialTicks) {
        @NotNull AxisAlignedBB aabb = entity.getEntityBoundingBox();
        float collisionBorderSize = entity.getCollisionBorderSize();

        if (collisionBorderSize != 0.0F) {
            aabb = aabb.grow(collisionBorderSize);
        }

        if (partialTicks != 1.0F) {
            double x = -(entity.posX - entity.lastTickPosX) * (1.0D - partialTicks);
            double y = -(entity.posY - entity.lastTickPosY) * (1.0D - partialTicks);
            double z = -(entity.posZ - entity.lastTickPosZ) * (1.0D - partialTicks);
            aabb = aabb.offset(x, y, z);
        }
        return aabb;
    }

    public static double distanceSq(@NotNull AxisAlignedBB aabb, @NotNull Vec3d vec) {
        double dx = MathHelper.clamp(vec.x, aabb.minX, aabb.maxX) - vec.x;
        double dy = MathHelper.clamp(vec.y, aabb.minY, aabb.maxY) - vec.y;
        double dz = MathHelper.clamp(vec.z, aabb.minZ, aabb.maxZ) - vec.z;
        return dx * dx + dy * dy + dz * dz;
    }
}

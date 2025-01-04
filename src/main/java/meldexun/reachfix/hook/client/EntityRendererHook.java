package meldexun.reachfix.hook.client;

import lombok.Data;
import meldexun.reachfix.config.ReachFixConfig;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public class EntityRendererHook {

    public static void getMouseOver(float partialTicks) {
        @NotNull Minecraft mc = Minecraft.getMinecraft();
        @Nullable Entity viewEntity = mc.getRenderViewEntity();

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

        // Potentially fixes an NPE.
        if (mc.objectMouseOver.entityHit == null) {
            mc.profiler.endSection();
            return;
        }

        mc.entityRenderer.pointedEntity = mc.objectMouseOver.entityHit;
        mc.pointedEntity = mc.objectMouseOver.entityHit;
        mc.profiler.endSection();
    }

    public static @NotNull RayTraceResult pointedObject(@NotNull Entity viewEntity, @NotNull EntityPlayer player,
                                                        @NotNull EnumHand hand, @NotNull World world, float partialTicks) {
        @NotNull Vec3d start = viewEntity.getPositionEyes(partialTicks);
        @NotNull Vec3d look = viewEntity.getLook(partialTicks);

        double blockReach = ReachFixUtil.getBlockReach(player, hand);
        double entityReach = ReachFixUtil.getEntityReach(player, hand);

        @NotNull Vec3d scale = look.scale(Math.max(blockReach, entityReach));
        @NotNull Vec3d end = start.add(scale);

        @Nullable RayTraceResult pointedBlock = world.rayTraceBlocks(start, end, false, false, false);
        @Nullable RayTraceResult pointedEntity = getPointedEntity(viewEntity, world, start, end, partialTicks);

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
        return new RayTraceResult(RayTraceResult.Type.MISS, end, null, new BlockPos(end));
    }

    private static @Nullable RayTraceResult getPointedEntity(@NotNull Entity viewEntity, @NotNull World world,
                                                             @NotNull Vec3d start, @NotNull Vec3d end,
                                                             float partialTicks) {
        @NotNull AxisAlignedBB aabb = new AxisAlignedBB(start, end).grow(1.0D);
        @NotNull Entity lowestRidingEntity = viewEntity.getLowestRidingEntity();

        @NotNull List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(viewEntity, aabb, entity -> {
            if (!EntitySelectors.NOT_SPECTATING.apply(entity)) {
                return false;
            }
            return entity.canBeCollidedWith();
        });

        @Nullable RayTraceResult result = null;
        @Nullable Entity pointedEntity = null;

        double min = Double.MAX_VALUE;

        for (@NotNull Entity entity : possibleEntities) {
            @NotNull AxisAlignedBB entityAabb = BoundingBoxUtil.getInteractionBoundingBox(entity, partialTicks);

            if (lowestRidingEntity == entity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                if (ReachFixConfig.getInstance().forceInteractionInsideVehicles && entityAabb.contains(start)) {
                    return new RayTraceResult(entity, start);
                }
                continue;
            }

            if (entityAabb.contains(start)) {
                return new RayTraceResult(entity, start);
            }

            @Nullable RayTraceResult rtr = entityAabb.calculateIntercept(start, end);

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

    private static boolean isNullOrMiss(@Nullable RayTraceResult result) {
        return result == null || result.typeOfHit == RayTraceResult.Type.MISS;
    }
}

package com.example.particlecomplex.utils.entity_utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityGetter {
    public static List<Entity> getEntitiesInCircle(Level level, double centerX, double centerY, double centerZ, double radius, boolean blockThroughWalls) {
        // 创建一个立方体区域来包含圆形区域
        AABB boundingBox = new AABB(
                centerX - radius, centerY - radius, centerZ - radius,
                centerX + radius, centerY + radius, centerZ + radius
        );

        // 使用 getEntitiesOfClass 方法来获取特定类型的实体
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, boundingBox, entity -> {
            // 计算与圆心的距离
            double distanceSquared = (entity.getX() - centerX) * (entity.getX() - centerX)
                    + (entity.getY() - centerY) * (entity.getY() - centerY)
                    + (entity.getZ() - centerZ) * (entity.getZ() - centerZ);

            // 如果在圆形区域外，直接过滤掉
            if (distanceSquared > radius * radius) {
                return false;
            }

            // 如果需要检测阻挡
            if (blockThroughWalls) {
                Vec3 centerPosition = new Vec3(centerX, centerY, centerZ);
                Vec3 entityPosition = entity.position();

                // 检查从中心到实体是否有阻挡方块
                BlockHitResult blockHit = level.clip(new ClipContext(
                        centerPosition, entityPosition,
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null
                ));

                // 若射线碰到方块且该方块在实体之前，则不包含该实体
                return blockHit.getType() != HitResult.Type.BLOCK ||
                        !(centerPosition.distanceToSqr(blockHit.getLocation()) < distanceSquared);
            }

            // 若无阻挡或不检测阻挡，返回 true 表示包含该实体
            return true;
        });

        return entities;
    }



    public static Entity getClosestEntityInSight(Player player, Double radius, boolean blockThroughWalls) {
        Level level = player.level();
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookDirection = player.getLookAngle();
        double distance = 100.0; // 设置最大视线距离

        // 计算视线的结束位置
        Vec3 endPosition = eyePosition.add(lookDirection.x * distance, lookDirection.y * distance, lookDirection.z * distance);

        // 创建一个 AABB 包围盒来包含视线的范围
        AABB boundingBox = new AABB(eyePosition, endPosition);

        // 获取所有在视线范围内的实体
        List<Entity> entities = level.getEntities(player, boundingBox, entity -> !entity.isSpectator() && entity.isPickable());

        // 找出最近的实体
        Entity closestEntity = null;
        double closestDistanceSquared = radius * radius;

        for (Entity entity : entities) {
            double distanceSquared = eyePosition.distanceToSqr(entity.position());
            if (distanceSquared < closestDistanceSquared) {
                if (blockThroughWalls) {
                    // 检查是否有方块阻挡视线
                    BlockHitResult blockHit = level.clip(new ClipContext(
                            eyePosition, entity.position(),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
                    ));

                    // 若射线碰到方块且该方块在实体之前，则跳过该实体
                    if (blockHit.getType() == HitResult.Type.BLOCK &&
                            eyePosition.distanceToSqr(blockHit.getLocation()) < distanceSquared) {
                        continue;
                    }
                }

                closestDistanceSquared = distanceSquared;
                closestEntity = entity;
            }
        }

        return closestEntity; // 返回视线最近的实体
    }
}


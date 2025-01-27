package com.example.particlecomplex.entities.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BaseEntityRenderer extends EntityRenderer<BaseEntity> {
    public BaseEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }



    @Override
    public ResourceLocation getTextureLocation(@NotNull BaseEntity entity) {
        // 返回null或一个空的纹理位置
        return null; // 或者 new ResourceLocation("");
    }

    @Override
    public void render(@NotNull BaseEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        // 不渲染任何内容
    }
}

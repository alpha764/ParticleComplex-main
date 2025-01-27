package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class SPORE_BLOSSOM_AIR extends BaseParticleType {

    public SPORE_BLOSSOM_AIR() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.SPORE_BLOSSOM_AIR.get();  // 返回自定义的粒子类型
    }
}

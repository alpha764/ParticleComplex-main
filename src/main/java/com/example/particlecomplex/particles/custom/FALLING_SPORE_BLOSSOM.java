package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class FALLING_SPORE_BLOSSOM extends BaseParticleType {

    public FALLING_SPORE_BLOSSOM() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.FALLING_SPORE_BLOSSOM.get();  // 返回自定义的粒子类型
    }
}

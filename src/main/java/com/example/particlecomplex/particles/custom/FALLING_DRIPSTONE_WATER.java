package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class FALLING_DRIPSTONE_WATER extends BaseParticleType {

    public FALLING_DRIPSTONE_WATER() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.FALLING_DRIPSTONE_WATER.get();  // 返回自定义的粒子类型
    }
}

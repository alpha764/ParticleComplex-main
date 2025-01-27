package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class DRIPPING_OBSIDIAN_TEAR extends BaseParticleType {

    public DRIPPING_OBSIDIAN_TEAR() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.DRIPPING_OBSIDIAN_TEAR.get();  // 返回自定义的粒子类型
    }
}

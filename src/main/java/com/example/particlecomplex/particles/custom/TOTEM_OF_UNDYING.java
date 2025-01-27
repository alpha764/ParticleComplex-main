package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class TOTEM_OF_UNDYING extends BaseParticleType {

    public TOTEM_OF_UNDYING() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.TOTEM_OF_UNDYING.get();  // 返回自定义的粒子类型
    }
}

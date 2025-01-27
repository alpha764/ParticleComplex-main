package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class AMBIENT_ENTITY_EFFECT extends BaseParticleType {

    public AMBIENT_ENTITY_EFFECT() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.AMBIENT_ENTITY_EFFECT.get();  // 返回自定义的粒子类型
    }
}

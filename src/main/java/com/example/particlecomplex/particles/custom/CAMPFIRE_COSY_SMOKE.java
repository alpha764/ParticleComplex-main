package com.example.particlecomplex.particles.custom;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.registry.ModParticleType;
import org.jetbrains.annotations.NotNull;

public class CAMPFIRE_COSY_SMOKE extends BaseParticleType {

    public CAMPFIRE_COSY_SMOKE() {
        super();  // 调用父类的构造函数
    }

    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.CAMPFIRE_COSY_SMOKE.get();  // 返回自定义的粒子类型
    }
}

package com.example.particlecomplex.particles.base;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class BaseParticleProvider implements ParticleProvider<BaseParticleType> {
    private final SpriteSet sprites;

    public BaseParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Nullable
    @Override
    public Particle createParticle(BaseParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        BaseParticle exampleParticle = new BaseParticle(pLevel, this.sprites, pX, pY, pZ, pType.getSpeed(), pType.getColor(),
                pType.getDiameter(), pType.getLifetime(), pType.getVecExpX(), pType.getVecExpY(), pType.getVecExpZ(), pType.getAx(),
                pType.getAy(), pType.getAz(), pType.getCenter().get(0), pType.getCenter().get(1), pType.getCenter().get(2),
                pType.getEntitiesID(), pType.getEntityID(), pType.getDynamicExp(),
                pType.getRotationX(), pType.getRotationY(), pType.getRotationZ(), new Random().nextDouble(),new Random().nextDouble(),new Random().nextDouble(),pType.getVecAx(),pType.getVecAy(),pType.getVecAz(),pType.getIsLocked(),pType.getAngle()[0],pType.getAngle()[1],pType.getAngle()[2]);
        exampleParticle.pickSprite(this.sprites);
        return exampleParticle;
    }


}

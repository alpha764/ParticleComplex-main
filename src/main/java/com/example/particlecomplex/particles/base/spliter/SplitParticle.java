package com.example.particlecomplex.particles.base.spliter;

import com.example.particlecomplex.particles.base.BaseParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import org.joml.Vector3d;
import org.joml.Vector4i;

import java.util.List;

public class SplitParticle extends BaseParticle {
    private final int columns;       // 每行贴图数
    private final int rows;          // 每列贴图数
    private final int totalFrames;   // 总帧数

    public SplitParticle(ClientLevel pLevel, SpriteSet spriteSet, double pX, double pY, double pZ, Vector3d speed, Vector4i color, double diameter, int lifetime, String vecExpX, String vecExpY, String vecExpZ, double a_x, double a_y, double a_z, double centerX, double centerY, double centerZ, List<Integer> entitiesID, int entityID, String dyexp, double rx, double ry, double rz, double internalRandomValueVecX, double internalRandomValueVecY, double internalRandomValueVecZ, String vecAx, String vecAy, String vecAz,  int columns, int rows, int totalFrames) {
        super(pLevel, spriteSet, pX, pY, pZ, speed, color, diameter, lifetime, vecExpX, vecExpY, vecExpZ, a_x, a_y, a_z, centerX, centerY, centerZ, entitiesID, entityID, dyexp, rx, ry, rz, internalRandomValueVecX, internalRandomValueVecY, internalRandomValueVecZ, vecAx, vecAy, vecAz);
        this.columns = columns;
        this.rows = rows;
        this.totalFrames = totalFrames;
    }
    @Override
    protected void updateSprite() {}

}

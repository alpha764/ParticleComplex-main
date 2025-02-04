package com.example.particlecomplex.particles.base;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.utils.expression_utils.ExpressionExtendBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector4i;

import java.util.*;

public class BaseParticle extends SimpleAnimatedParticle {

    private final double internalRandomValueVecX;
    private final double internalRandomValueVecY;
    private final double internalRandomValueVecZ;
    private static final double PARTICLE_SCALE_FOR_ONE_METRE = 0.5f;
    private double rz;
    private double ry;
    private double rx;
    private int colorX;
    private int colorY;
    private int colorZ;
    private int colorW;
    final String vecAx;
    final String vecAy;
    final String vecAz;
    protected double age;



    private double ax;
    private double ay;
    private double az;
    private final List<Integer> entitiesID;
    private final double ydo;
    private final double xdo;
    private final double zdo;
    private double diameter;
    private Vector4i color;
    private String dyexp;

    public double originX;
    public double originY;
    public double originZ;
    private double centerX;
    private double centerY;
    private double centerZ;
    private String vecExpX;
    private String vecExpY;
    private String vecExpZ;
    private final int entityID;
    private final List<String> DynamicProperties = new ArrayList<>();
    private boolean isLocked=false;
    private double pitchX;
    private double yawY;
    private double rollZ;


    public BaseParticle(ClientLevel pLevel, SpriteSet spriteSet, double pX, double pY, double pZ, Vector3d speed, Vector4i color,
                        double diameter, int lifetime, String vecExpX, String vecExpY, String vecExpZ,
                        double a_x, double a_y, double a_z, double centerX, double centerY, double centerZ,
                        List<Integer> entitiesID, int entityID, String dyexp, double rx, double ry, double rz,
                        double internalRandomValueVecX,double internalRandomValueVecY,double internalRandomValueVecZ,String vecAx,String vecAy,
                        String vecAz,boolean isLocked,double pitchX,double yaw,double rollZ) {
        super(pLevel, pX, pY, pZ, spriteSet, 0);
        this.lifetime = lifetime;
        this.xd = speed.x;
        this.yd = speed.y;
        this.zd = speed.z;
        this.ydo = this.yd;
        this.xdo = this.xd;
        this.zdo = this.zd;
        this.ax = a_x;
        this.ay = a_y;
        this.az = a_z;
        this.vecExpX = vecExpX;
        this.vecExpY = vecExpY;
        this.vecExpZ = vecExpZ;
        if (!(color.x == 100 && color.y == 100 && color.z == 100))
            this.setColor(color.x / 255F, color.y / 255F, color.z / 255F);
        this.color = color;
        this.colorX = color.x;
        this.colorY = color.y;
        this.colorZ = color.z;
        this.colorW = color.w;
        this.setAlpha(this.color.w / 255F);
        this.diameter = diameter;
        this.quadSize = (float) (PARTICLE_SCALE_FOR_ONE_METRE * this.diameter);
        this.hasPhysics = true;
        this.gravity = 0;
        this.originX = this.x;
        this.originY = this.y;
        this.originZ = this.z;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.entitiesID = entitiesID;
        this.entityID = entityID;
        this.friction = 1F;
        this.dyexp = dyexp;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.internalRandomValueVecX = internalRandomValueVecX;
        this.internalRandomValueVecY = internalRandomValueVecY;
        this.internalRandomValueVecZ = internalRandomValueVecZ;
        this.vecAx=vecAx;
        this.vecAy=vecAy;
        this.vecAz=vecAz;
        this.isLocked=isLocked;
        this.pitchX=pitchX;
        this.yawY =yaw;
        this.rollZ=rollZ;
    }

    protected void updateSprite() {
        if (this.age != 0 && this.lifetime != 0) {
            this.setSprite(this.sprites.get((int)this.age, this.lifetime));
        }
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void updateOrigin() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.age+=1D;
        if (this.age >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double) this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public void fixVelocityByExpression() {
        if (vecExpX.isEmpty()) {
            this.vecExpX="0";
            ExampleMod.LOGGER.error("X速度表达式为空");
        }
        if (vecExpY.isEmpty()) {
            this.vecExpY="0";
            ExampleMod.LOGGER.error("Y速度表达式为空");
        }
        if (vecExpZ.isEmpty()) {
            this.vecExpZ="0";
            ExampleMod.LOGGER.error("Z速度表达式为空");
        }
        try {
            Expression expX = new ExpressionExtendBuilder(vecExpX)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("pt")
                    .variable("sRandom")
                    .build();


            this.xd = expX.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("sRandom", internalRandomValueVecX)
                    .setVariable("pt",this.age/this.lifetime)
                    .evaluate();


            Expression expY = new ExpressionExtendBuilder(vecExpY)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("pt")
                    .variable("sRandom")
                    .build();
            this.yd += expY.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("sRandom", internalRandomValueVecY)
                    .setVariable("pt",this.age/this.lifetime)
                    .evaluate();
        } catch (Exception e) {
            System.err.println("错误的参数格式");
        }

        Expression expZ = new ExpressionExtendBuilder(vecExpZ)
                .variable("lifetime")
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .variable("sRandom")
                .variable("pt")

                .build();
        this.zd = expZ.setVariable("t", this.age)
                .setVariable("x", this.getParticleRelativePos().get(0))
                .setVariable("y", this.getParticleRelativePos().get(1))
                .setVariable("z", this.getParticleRelativePos().get(2))
                .setVariable("lifetime", this.lifetime)
                .setVariable("sRandom", internalRandomValueVecZ)
                .setVariable("pt",this.age/this.lifetime)
                .evaluate();
    }

    public double fixVelocityByExpression(List<Integer> entitiesID, String vecExp) {
        if (this.entitiesID != null && !this.entitiesID.isEmpty()) {
            ExpressionBuilder exp_Builder = new ExpressionExtendBuilder(vecExp)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("pt")
                    .variable("sRandom");

            for (int i = 0; i < entitiesID.size(); i++) {
                if (level.getEntity(entitiesID.get(i)) != null) {
                    exp_Builder
                            .variable("ex" + i)
                            .variable("ey" + i)
                            .variable("ez" + i);
                }
            }
            Expression exp_ = exp_Builder.build();
            exp_.setVariable("t", this.age)
                    .setVariable("lifetime", this.getLifetime())
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("pt",this.age/this.lifetime)
                    .setVariable("z", this.getParticleRelativePos().get(2));

            if(Objects.equals(vecExp, this.vecExpX))exp_.setVariable("sRandom",this.internalRandomValueVecX);
            if(Objects.equals(vecExp, this.vecExpY))exp_.setVariable("sRandom",this.internalRandomValueVecY);
            if(Objects.equals(vecExp, this.vecExpZ))exp_.setVariable("sRandom",this.internalRandomValueVecZ);
            for (int i = 0; i < entitiesID.size(); i++) {
                if (level.getEntity(entitiesID.get(i)) != null) {
                    exp_.setVariable("ex" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getX() - centerX)
                            .setVariable("ey" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getY() - centerY)
                            .setVariable("ez" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getZ() - centerZ);

                }
            }
            return exp_.evaluate();
        }
        ExampleMod.LOGGER.error("Entity is null");
        return 0;
    }
    public void fixAccelerationByExpression() {
        if(vecAx==null||vecAy==null||vecAz==null){return;} //TODO错误处理
        if (vecAx.isEmpty()) {
            ExampleMod.LOGGER.error("X加速度表达式为空");
        }
        if (vecAy.isEmpty()) {
            ExampleMod.LOGGER.error("Y加速度表达式为空");
        }
        if (vecAz.isEmpty()) {

            ExampleMod.LOGGER.error("Z加速度表达式为空");
        }
        try {
            Expression expX = new ExpressionExtendBuilder(vecAx)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("sRandom")
                    .variable("vx")
                    .variable("vy")
                    .variable("vz")
                    .variable("pt")
                    .build();


            this.ax = expX.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("vx",this.xd)
                    .setVariable("vy",this.yd)
                    .setVariable("vz",this.zd)
                    .setVariable("sRandom", internalRandomValueVecX)
                    .setVariable("pt",this.age/this.lifetime)
                    .evaluate();


            Expression expY = new ExpressionExtendBuilder(vecAy)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("sRandom")
                    .variable("vx")
                    .variable("vy")
                    .variable("vz")
                    .variable("pt")
                    .build();

            this.ay += expY.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("vx",this.xd)
                    .setVariable("vy",this.yd)
                    .setVariable("vz",this.zd)
                    .setVariable("sRandom", internalRandomValueVecY)
                    .setVariable("pt",this.age/this.lifetime)
                    .evaluate();
        } catch (Exception e) {
            System.err.println("错误的参数格式");
        }

        Expression expZ = new ExpressionExtendBuilder(vecAz)
                .variable("lifetime")
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .variable("sRandom")
                .variable("vx")
                .variable("vy")
                .variable("vz")
                .variable("pt")
                .build();
        this.az = expZ.setVariable("t", this.age)
                .setVariable("x", this.getParticleRelativePos().get(0))
                .setVariable("y", this.getParticleRelativePos().get(1))
                .setVariable("z", this.getParticleRelativePos().get(2))
                .setVariable("vx",this.xd)
                .setVariable("vy",this.yd)
                .setVariable("vz",this.zd)
                .setVariable("lifetime", this.lifetime)
                .setVariable("sRandom", internalRandomValueVecZ)
                .setVariable("pt",this.age/this.lifetime)
                .evaluate();

    }

    public double fixAccelerationByExpression(List<Integer> entitiesID, String vecA) {
        if(vecAx==null||vecAy==null||vecAz==null){return 0;} //TODO 继续处理
        if (this.entitiesID != null && !this.entitiesID.isEmpty()) {
            ExpressionBuilder exp_Builder = new ExpressionExtendBuilder(vecA)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("vx")
                    .variable("vy")
                    .variable("vz")
                    .variable("pt")

                    .variable("sRandom");

            for (int i = 0; i < entitiesID.size(); i++) {
                if (level.getEntity(entitiesID.get(i)) != null) {
                    exp_Builder
                            .variable("ex" + i)
                            .variable("ey" + i)
                            .variable("ez" + i);
                }
            }
            Expression exp_ = exp_Builder.build();
            exp_.setVariable("t", this.age)
                    .setVariable("lifetime", this.getLifetime())
                    .setVariable("vx",this.xd)
                    .setVariable("vy",this.yd)
                    .setVariable("vz",this.zd)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("pt",this.age/this.lifetime)
                    .setVariable("z", this.getParticleRelativePos().get(2));


            for (int i = 0; i < entitiesID.size(); i++) {
                if (level.getEntity(entitiesID.get(i)) != null) {
                    exp_.setVariable("ex" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getX() - centerX)
                            .setVariable("ey" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getY() - centerY)
                            .setVariable("ez" + i, Objects.requireNonNull(level.getEntity(entitiesID.get(i))).getZ() - centerZ);

                }
            }
            return exp_.evaluate();
        }
        ExampleMod.LOGGER.error("Entity is null");
        return 0;
    }

    public void UpdateDynamicExpToFactory() {
        if (dyexp == null || dyexp.trim().isEmpty()) {
            dyexp = null;
        }

        if (dyexp != null) {
            String[] parts = dyexp.split(";");
            for (String part : parts) {
                // 去除可能的前后空格，并添加到 DynamicProperties 列表中
                String trimmedPart = part.trim();
                if (!trimmedPart.isEmpty()) {
                    DynamicProperties.add(trimmedPart);
                }
            }
            dyexp = null;
        }
    }

    public List<String> splitString(String input) {
        String[] parts = input.split("<-");
        if (parts.length == 2) {
            return Arrays.stream(parts).map(String::trim).toList();
        } else {
            ExampleMod.LOGGER.error("输入字符串格式不正确。");
            return null;
        }
    }

    public static double[] rotateX(double ignoredCenterX, double centerY, double centerZ,
                                   double ignoredX, double Y, double Z, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);

        // 移动到以 center 为原点的坐标系
        double translatedY = Y - centerY;
        double translatedZ = Z - centerZ;

        // 使用绕 X 轴的旋转矩阵旋转
        double rotatedY = translatedY * Math.cos(angleRadians) - translatedZ * Math.sin(angleRadians);
        double rotatedZ = translatedY * Math.sin(angleRadians) + translatedZ * Math.cos(angleRadians);

        // 移回原来的坐标系
        rotatedY += centerY;
        rotatedZ += centerZ;

        // 返回从原始点到旋转后点的向量
        return new double[]{0, rotatedY - Y, rotatedZ - Z};
    }

    // 绕 Y 轴旋转
    public static double[] rotateY(double centerX, double ignoredCenterY, double centerZ,
                                   double X, double ignoredY, double Z, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);

        // 移动到以 center 为原点的坐标系
        double translatedX = X - centerX;
        double translatedZ = Z - centerZ;

        // 使用绕 Y 轴的旋转矩阵旋转
        double rotatedX = translatedX * Math.cos(angleRadians) + translatedZ * Math.sin(angleRadians);
        double rotatedZ = -translatedX * Math.sin(angleRadians) + translatedZ * Math.cos(angleRadians);

        // 移回原来的坐标系
        rotatedX += centerX;
        rotatedZ += centerZ;

        // 返回从原始点到旋转后点的向量
        return new double[]{rotatedX - X, 0, rotatedZ - Z};
    }

    // 绕 Z 轴旋转
    public static double[] rotateZ(double centerX, double centerY, double ignoredCenterZ,
                                   double X, double Y, double ignoredZ, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);

        // 移动到以 center 为原点的坐标系
        double translatedX = X - centerX;
        double translatedY = Y - centerY;

        // 使用绕 Z 轴的旋转矩阵旋转
        double rotatedX = translatedX * Math.cos(angleRadians) - translatedY * Math.sin(angleRadians);
        double rotatedY = translatedX * Math.sin(angleRadians) + translatedY * Math.cos(angleRadians);

        // 移回原来的坐标系
        rotatedX += centerX;
        rotatedY += centerY;

        // 返回从原始点到旋转后点的向量
        return new double[]{rotatedX - X, rotatedY - Y, 0};
    }

    public void rotate() {
        double v1x = rotateX(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rx)[0];
        double v1y = rotateX(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rx)[1];
        double v1z = rotateX(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rx)[2];
        double v2x = rotateY(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.ry)[0];
        double v2y = rotateY(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.ry)[1];
        double v2z = rotateY(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.ry)[2];
        double v3x = rotateZ(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rz)[0];
        double v3y = rotateZ(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rz)[1];
        double v3z = rotateZ(this.centerX, this.centerY, this.centerZ, this.x, this.y, this.z, this.rz)[2];
        this.xd = this.xd + v1x + v2x + v3x;
        this.yd = this.yd + v1y + v2y + v3y;
        this.zd = this.zd + v1z + v2z + v3z;
    }


    /**
     * 直接设置粒子绕各轴的旋转角度（立即生效）
     * @param pitchX 绕X轴旋转角度（度）
     * @param yawY   绕Y轴旋转角度（度）
     * @param rollZ  绕Z轴旋转角度（度）
     */
    public void setRotationAngles(double pitchX, double yawY, double rollZ) {
        // 计算相对于初始位置的旋转偏移
        double[] rotatedX = rotateX(this.centerX, this.centerY, this.centerZ,
                this.originX, this.originY, this.originZ, pitchX);
        double[] rotatedXY = rotateY(this.centerX, this.centerY, this.centerZ,
                this.originX + rotatedX[0],
                this.originY + rotatedX[1],
                this.originZ + rotatedX[2], yawY);
        double[] rotatedXYZ = rotateZ(this.centerX, this.centerY, this.centerZ,
                this.originX + rotatedX[0] + rotatedXY[0],
                this.originY + rotatedX[1] + rotatedXY[1],
                this.originZ + rotatedX[2] + rotatedXY[2], rollZ);

        // 应用最终旋转偏移
        this.x = this.originX + rotatedX[0] + rotatedXY[0] + rotatedXYZ[0];
        this.y = this.originY + rotatedX[1] + rotatedXY[1] + rotatedXYZ[1];
        this.z = this.originZ + rotatedX[2] + rotatedXY[2] + rotatedXYZ[2];

        // 重置角速度（可选）
        this.rx = 0;
        this.ry = 0;
        this.rz = 0;
    }

    // 新增辅助方法
    private double[] getOriginPosition() {
        return new double[]{this.originX, this.originY, this.originZ};
    }

    private void resetToOrigin() {
        this.x = this.originX;
        this.y = this.originY;
        this.z = this.originZ;
    }


    public void stringMap(String y, String Exp) {
        //控制自变量的类型
        Map<String, Double> variables = new HashMap<>();
        variables.put("ax", this.ax);
        variables.put("ay", this.ay);
        variables.put("az", this.az);
        variables.put("centerX", this.centerX);
        variables.put("centerY", this.centerY);
        variables.put("centerZ", this.centerZ);
        variables.put("vx", this.xd);
        variables.put("vy", this.yd);
        variables.put("vz", this.zd);
        variables.put("x", this.x);
        variables.put("y", this.y);
        variables.put("z", this.z);
        variables.put("r", (double) this.color.x);
        variables.put("g", (double) this.color.y);
        variables.put("b", (double) this.color.z);
        variables.put("w", (double) this.color.w);
        variables.put("t", (double) this.age);
        variables.put("lifetime", (double) this.lifetime);
        variables.put("originX", this.originX);
        variables.put("originY", this.originY);
        variables.put("originZ", this.originZ);
        variables.put("random1", this.internalRandomValueVecX);
        variables.put("random2", this.internalRandomValueVecY);
        variables.put("random3", this.internalRandomValueVecZ);
        variables.put("pt",this.age/this.lifetime);
        variables.put("pitchX",this.pitchX);
        variables.put("yawY",this.yawY);
        variables.put("rollZ",this.rollZ);
        //特殊:实体自变量
        for (String i : Arrays.asList("x", "y", "z")) {
            for (int j = 0; j < entitiesID.size(); j++) {
                if (i.equals("x")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level.getEntity(entitiesID.get(j))).getX());
                }
                if (i.equals("y")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level.getEntity(entitiesID.get(j))).getY());
                }
                if (i.equals("z")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level.getEntity(entitiesID.get(j))).getZ());
                }
            }
        }
        ExpressionExtendBuilder builder = new ExpressionExtendBuilder(Exp);


        for (String key : variables.keySet()) {
            builder.variable(key);
        }

        Expression e_ = builder.build();

        //应变量控制
        switch (y) {
            case "ax":
                this.ax =  e_.setVariables(variables).evaluate();
                break;
            case "ay":
                this.ay =  e_.setVariables(variables).evaluate();
                break;
            case "az":
                this.az =  e_.setVariables(variables).evaluate();
                break;
            case "centerX":
                this.centerX = e_.setVariables(variables).evaluate();
                break;
            case "centerY":
                this.centerY = e_.setVariables(variables).evaluate();
                break;
            case "centerZ":
                this.centerZ = e_.setVariables(variables).evaluate();
                break;
            case "diameter":
                this.diameter =  e_.setVariables(variables).evaluate();
                break;
            case "vx":
                this.xd += e_.setVariables(variables).evaluate();
                break;
            case "vy":
                this.yd += e_.setVariables(variables).evaluate();
                break;
            case "vz":
                this.zd += e_.setVariables(variables).evaluate();
                break;
            case "x":
                this.x = e_.setVariables(variables).evaluate() + centerX;
                break;
            case "y":
                this.y = e_.setVariables(variables).evaluate() + centerY;
                break;
            case "z":
                this.z = e_.setVariables(variables).evaluate() + centerZ;
                break;
            case "r":
                this.colorX = (int) e_.setVariables(variables).evaluate();
                break;
            case "g":
                this.colorY = (int) e_.setVariables(variables).evaluate();
                break;
            case "b":
                this.colorZ = (int) e_.setVariables(variables).evaluate();
                break;
            case "w":
                this.colorW = (int) e_.setVariables(variables).evaluate();
                break;
            case "rx":
                this.rx = e_.setVariables(variables).evaluate();
                break;
            case "ry":
                this.ry = e_.setVariables(variables).evaluate();
                break;
            case "rz":
                this.rz = e_.setVariables(variables).evaluate();
                break;
            case "lifetime":
                this.lifetime = (int) e_.setVariables(variables).evaluate();
                break;
            case "t":
                this.age = (int) e_.setVariables(variables).evaluate();
                break;
            case "pitchX":
                this.pitchX=e_.setVariables(variables).evaluate();
                break;
            case"yawY":
                this.yawY=e_.setVariables(variables).evaluate();
                break;
            case"rollZ":
                this.rollZ=e_.setVariables(variables).evaluate();
                break;
            case"transX":
                this.translate(new Vec3(e_.setVariables(variables).evaluate(),this.centerY,this.centerZ));
                break;
            case"transY":
                this.translate(new Vec3(this.centerX,e_.setVariables(variables).evaluate(),this.centerZ));
                break;
            case"transZ":
                this.translate(new Vec3(this.centerX,this.centerY,e_.setVariables(variables).evaluate()));
                break;
            default:
                throw new IllegalArgumentException("Invalid variable: " + y);
        }
        this.quadSize = (float) (PARTICLE_SCALE_FOR_ONE_METRE * this.diameter);
        this.color = new Vector4i(colorX, colorY, colorZ, this.colorW);
        if (!(colorX == 100 && colorY == 100 && colorZ == 100)) {
            this.setColor(colorX / 255F, colorY / 255F, colorZ / 255F);
        }
        this.setAlpha(this.colorW / 255F);
    }

    //平到某一个点
    public void translate(Vec3 pos){
        double deltaX=pos.x-centerX;
        double deltaY=pos.y-centerY;
        double deltaZ=pos.z-centerZ;

        this.centerX=pos.x;
        this.centerY=pos.y;
        this.centerZ=pos.z;
        this.setPos(this.x + deltaX,this.y+deltaY,this.z+deltaZ);
    }

    public void fixVelocityByAcceleration() {
        this.xd += this.ax / 20 * this.age;
        this.yd += this.ay / 20 * this.age;
        this.zd += this.az / 20  * this.age;
    }

    public List<Double> getParticleRelativePos() {
        List<Double> relativePos = new ArrayList<>();
        relativePos.add(this.x - centerX);
        relativePos.add(this.y - centerY);
        relativePos.add(this.z - centerZ);
        return relativePos;
    }
    private void dynamicProcess(){
        for (String DynamicProperty : DynamicProperties) {
            try {
                stringMap(splitString(DynamicProperty).get(0), splitString(DynamicProperty).get(1));
            } catch (Exception e) {
                if (this.age % 100 == 0)
                    if (Minecraft.getInstance().player != null) {
                        System.err.println("发射错误:"+e);
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("未知表达式,跳过属性映射"));
                    }
            }
        }
    }
    private void fixVecExpWithEntity(){
        if (this.entitiesID != null && !this.entitiesID.isEmpty()) {
            try {
                this.xd+=fixVelocityByExpression(this.entitiesID, this.vecExpX);
                this.yd+=fixVelocityByExpression(this.entitiesID, this.vecExpY);
                this.zd+=fixVelocityByExpression(this.entitiesID, this.vecExpZ);

            } catch (UnknownFunctionOrVariableException e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("无效的速度表达式,跳过单次速度表达式映射"));
                    }
                }
            } catch (ArithmeticException e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("除零错误,跳过单次速度表达式映射"));
                    }
                }
            } catch (Exception e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("发生"+e.getClass()+"的 "+e.getMessage()+" 错误,跳过单次速度表达式映射"));
                    }
                }
            }

        }
    }
    private void fixAExpWithEntity(){
        if (this.entitiesID != null && !this.entitiesID.isEmpty()) {
            try {
                this.ax+=fixAccelerationByExpression(this.entitiesID,this.vecAx);
                this.ay+=fixAccelerationByExpression(this.entitiesID,this.vecAy);
                this.az+=fixAccelerationByExpression(this.entitiesID,this.vecAz);

            } catch (UnknownFunctionOrVariableException e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("无效的速度表达式,跳过单次速度表达式映射"));
                    }
                }
            } catch (ArithmeticException e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("除零错误,跳过单次速度表达式映射"));
                    }
                }
            } catch (Exception e) {
                if (this.age % 100 == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("发生"+e.getClass()+"的 "+e.getMessage()+" 错误,跳过单次速度表达式映射"));
                    }
                }
            }

        }
    }
    private void fixVecExpWithoutEntity(){
        if (entitiesID != null && entityID == 0 && entitiesID.isEmpty()) {
            fixVelocityByExpression();
        }
    }
    private void fixAExpWithoutEntity(){
        if (entitiesID != null && entityID == 0 && entitiesID.isEmpty()) {
            fixAccelerationByExpression();
        }
    }

    @Override
    public void tick() {

        this.zd = zdo;
        this.yd = ydo;
        this.xd = xdo;
        if(!isLocked){
            if(this.pitchX==-1&&this.rollZ==-1&&this.yawY==-1) {}else {
                setRotationAngles(pitchX,yawY,rollZ);
            }
            updateSprite(); // 更新纹理


            UpdateDynamicExpToFactory(); // 把dyExp切割,然后清空dyExp
            dynamicProcess();  // 处理切割后的动态属性映射
            fixVecExpWithEntity(); // 有实体的速度表达式更新
            fixVecExpWithoutEntity(); // 无实体的速度表达式更新
            rotate(); // 旋转
            fixVelocityByAcceleration(); // 根据 v=at 修改速度
            fixAExpWithEntity(); //  有实体的加速度表达式更新(有v)
            fixAExpWithoutEntity(); // 无实体的加速度表达式更新(有v)
            updateOrigin(); // 根据 x=vt 修改位移

        this.zd = 0;
        this.yd = 0;
        this.xd = 0;
        }

    }

}



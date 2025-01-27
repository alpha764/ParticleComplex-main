package com.example.particlecomplex.entities.base;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.utils.expression_utils.ExpressionExtendBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.*;

public class BaseEntity extends Entity {
    private Vector3d speed;
    private double rz;
    private double ry;
    private double rx;

    private double xd;
    private double yd;
    private double zd;
    private int lifetime;
    String vecAx;
    String vecAy;
    String vecAz;
    private double age;


    private double ax;
    private double ay;
    private double az;
    private List<Integer> entitiesID=new ArrayList<>(); //只能是服务端的entitiesID
    private double ydo;
    private double xdo;
    private double zdo;
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
    private List<Integer> clientEntitiesID;
    private List<Integer> serverEntitiesID;

    public BaseEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lifetime = 80;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.speed=new Vector3d(0,0,0);
        this.ydo = 0;
        this.xdo = 0;
        this.zdo = 0;
        this.ax = 0;
        this.ay = 0;
        this.az = 0;
        this.vecExpX = "0";
        this.vecExpY = "1";
        this.vecExpZ = "0";
        this.originX = this.getX();
        this.originY = this.getY();
        this.originZ = this.getZ();
        this.centerX = this.getX();
        this.centerY = this.getY();
        this.centerZ = this.getZ();
        this.entityID = 0;
        this.dyexp = "t<-t";
        this.rx = 0;
        this.ry = 0;
        this.rz = 0;
        this.vecAx="0";
        this.vecAy="0";
        this.vecAz="0";
    }


    public void updateOrigin() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.age+=1;
        if (this.age >= this.lifetime) {
            this.discard();
        } else {
            this.setPos(this.getX() + this.xd,this.getY() + this.yd,this.getZ() + this.zd);
        }
    }

    public void fixVelocityByExpression() {
        System.out.println(this.getY());
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
                    .build();


            this.xd = expX.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .evaluate();


            Expression expY = new ExpressionExtendBuilder(vecExpY)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .build();
            this.yd += expY.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
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
                .build();
        this.zd = expZ.setVariable("t", this.age)
                .setVariable("x", this.getParticleRelativePos().get(0))
                .setVariable("y", this.getParticleRelativePos().get(1))
                .setVariable("z", this.getParticleRelativePos().get(2))
                .setVariable("lifetime", this.lifetime)
                .evaluate();
    }

    public double fixVelocityByExpression(List<Integer> entitiesID, String vecExp) {
        if (this.entitiesID != null && !this.entitiesID.isEmpty()) {
            ExpressionBuilder exp_Builder = new ExpressionExtendBuilder(vecExp)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z");

            for (int i = 0; i < entitiesID.size(); i++) {
                if (level().getEntity(entitiesID.get(i)) != null) {
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
                    .setVariable("z", this.getParticleRelativePos().get(2));

            for (int i = 0; i < entitiesID.size(); i++) {
                if (level().getEntity(entitiesID.get(i)) != null) {
                    exp_.setVariable("ex" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getX() - centerX)
                            .setVariable("ey" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getY() - centerY)
                            .setVariable("ez" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getZ() - centerZ);

                }
            }
            return exp_.evaluate();
        }
        ExampleMod.LOGGER.error("Entity is null");
        return 0;
    }

    private int getLifetime() {
        return lifetime;
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
                    .variable("vx")
                    .variable("vy")
                    .variable("vz")
                    .build();


            this.ax = expX.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("vx",this.xd)
                    .setVariable("vy",this.yd)
                    .setVariable("vz",this.zd)
                    .evaluate();


            Expression expY = new ExpressionExtendBuilder(vecAy)
                    .variable("lifetime")
                    .variable("t")
                    .variable("x")
                    .variable("y")
                    .variable("z")
                    .variable("vx")
                    .variable("vy")
                    .variable("vz")
                    .build();

            this.ay += expY.setVariable("t", this.age)
                    .setVariable("lifetime", this.lifetime)
                    .setVariable("x", this.getParticleRelativePos().get(0))
                    .setVariable("y", this.getParticleRelativePos().get(1))
                    .setVariable("z", this.getParticleRelativePos().get(2))
                    .setVariable("vx",this.xd)
                    .setVariable("vy",this.yd)
                    .setVariable("vz",this.zd)
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
                .variable("vx")
                .variable("vy")
                .variable("vz")
                .build();
        this.az = expZ.setVariable("t", this.age)
                .setVariable("x", this.getParticleRelativePos().get(0))
                .setVariable("y", this.getParticleRelativePos().get(1))
                .setVariable("z", this.getParticleRelativePos().get(2))
                .setVariable("vx",this.xd)
                .setVariable("vy",this.yd)
                .setVariable("vz",this.zd)
                .setVariable("lifetime", this.lifetime)
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
                    .variable("vz");

            for (int i = 0; i < entitiesID.size(); i++) {
                if (level().getEntity(entitiesID.get(i)) != null) {
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
                    .setVariable("z", this.getParticleRelativePos().get(2));


            for (int i = 0; i < entitiesID.size(); i++) {
                if (level().getEntity(entitiesID.get(i)) != null) {
                    exp_.setVariable("ex" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getX() - centerX)
                            .setVariable("ey" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getY() - centerY)
                            .setVariable("ez" + i, Objects.requireNonNull(level().getEntity(entitiesID.get(i))).getZ() - centerZ);

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
        double v1x = rotateX(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rx)[0];
        double v1y = rotateX(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rx)[1];
        double v1z = rotateX(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rx)[2];
        double v2x = rotateY(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.ry)[0];
        double v2y = rotateY(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.ry)[1];
        double v2z = rotateY(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.ry)[2];
        double v3x = rotateZ(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rz)[0];
        double v3y = rotateZ(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rz)[1];
        double v3z = rotateZ(this.centerX, this.centerY, this.centerZ, this.getX(), this.getY(), this.getZ(), this.rz)[2];
        this.xd = this.xd + v1x + v2x + v3x;
        this.yd = this.yd + v1y + v2y + v3y;
        this.zd = this.zd + v1z + v2z + v3z;
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
        variables.put("x", this.getX());
        variables.put("y", this.getY());
        variables.put("z", this.getZ());
        variables.put("t", this.age);
        variables.put("lifetime", (double) this.lifetime);
        variables.put("originX", this.originX);
        variables.put("originY", this.originY);
        variables.put("originZ", this.originZ);

        //特殊:实体自变量
        for (String i : Arrays.asList("x", "y", "z")) {
            for (int j = 0; j < entitiesID.size(); j++) {
                if (i.equals("x")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level().getEntity(entitiesID.get(j))).getX());
                }
                if (i.equals("y")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level().getEntity(entitiesID.get(j))).getY());
                }
                if (i.equals("z")) {
                    variables.put("e" + j + i, Objects.requireNonNull(level().getEntity(entitiesID.get(j))).getZ());
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
                ExampleMod.LOGGER.warn("不建议将x作为应变量,可能导致意料之外后果,建议采用ParticleSpawner");
                this.setPos(new Vec3(e_.setVariables(variables).evaluate() + centerX,getY(),getZ()));
                break;
            case "y":
                ExampleMod.LOGGER.warn("不建议将y作为应变量,可能导致意料之外后果,建议采用ParticleSpawner");
                this.setPos(new Vec3(getX(),e_.setVariables(variables).evaluate() + centerY,getZ()));
                break;
            case "z":
                ExampleMod.LOGGER.warn("不建议将z作为应变量,可能导致意料之外后果,建议采用ParticleSpawner");
                this.setPos(new Vec3(getX(),getY(),e_.setVariables(variables).evaluate() + centerZ));
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
            default:
                throw new IllegalArgumentException("Invalid variable: " + y);
        }
    }

    public void fixVelocityByAcceleration() {
        this.xd += this.ax / 20  * this.age;
        this.yd += this.ay / 20  * this.age;
        this.zd += this.az / 20  * this.age;
    }

    public List<Double> getParticleRelativePos() {
        List<Double> relativePos = new ArrayList<>();
        relativePos.add(this.getX() - centerX);
        relativePos.add(this.getY() - centerY);
        relativePos.add(this.getZ() - centerZ);
        return relativePos;
    }
    private void dynamicProcess(){
        for (String DynamicProperty : DynamicProperties) {
            try {
                stringMap(splitString(DynamicProperty).get(0), splitString(DynamicProperty).get(1));
            } catch (Exception e) {
                if (this.age % 100 == 0)
                    if (Minecraft.getInstance().player != null) {
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
    public void setSpeed(double vx,double vy,double vz){
        this.xdo=vx;
        this.ydo=vy;
        this.zdo=vz;
    }
    public void setLifetime(int lifetime){
        this.lifetime=lifetime;
    }

    public BaseEntity setRotation(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        return this;
    }
    public BaseEntity setEntitiesID(List<Integer> entitiesID) {
        Level levelT =this.level();
        if(!levelT.isClientSide){
            this.entitiesID = entitiesID.stream().toList();
        }
        return this;
    }
    public BaseEntity setVecExp(String vecExpX,String vecExpY,String vecExpZ){
        this.vecExpX=vecExpX;
        this.vecExpY=vecExpY;
        this.vecExpZ=vecExpZ;
        return this;
    }
    public BaseEntity setVecA(String vecExpAX,String vecExpAY,String vecExpAZ){
        this.vecAx=vecExpAX;
        this.vecAy=vecExpAY;
        this.vecAz=vecExpAZ;
        return this;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }
    public double getAz(){
        return az;
    }
    public Vector3d getSpeed() {
        return this.speed;
    }

    @Override
    public void tick() {

        this.zd = zdo;
        this.yd = ydo;
        this.xd = xdo;


        UpdateDynamicExpToFactory(); // 把dyExp切割,然后清空dyExp
        dynamicProcess();  // 处理切割后的动态属性映射
        fixVecExpWithEntity(); // 有实体的速度表达式更新
        fixVecExpWithoutEntity(); // 无实体的速度表达式更新

        rotate(); // 旋转
        fixVelocityByAcceleration(); // 根据 v=at 修改速度
        fixAExpWithEntity(); //  有实体的加速度表达式更新(有v)
        fixAExpWithoutEntity(); // 无实体的加速度表达式更新(有v)
        updateOrigin(); // 根据 x=vt 修改位移
        this.speed=new Vector3d(this.xd,this.yd,this.zd);
        this.zd = 0;
        this.yd = 0;
        this.xd = 0;

    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }
}

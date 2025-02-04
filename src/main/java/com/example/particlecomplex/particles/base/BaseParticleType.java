package com.example.particlecomplex.particles.base;

import com.example.particlecomplex.registry.ModParticleType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.joml.Math.clamp;

public class BaseParticleType extends ParticleType<BaseParticleType> implements ParticleOptions {


    private double rx;
    private double ry;
    private double rz;
    private float fps = 1;
    private int entityID;
    private Vector3d speed;
    private Vector4i color;
    private float diameter;
    private int lifetime;
    private String vecExpX;//速度
    private String vecExpY;
    private String vecExpZ;
    private List<Integer> entitiesID;
    private double ax;
    private double ay;
    private double az;
    private double centerX;
    private double centerY;
    private double centerZ;
    private String dynamicExp;
    private String vecAx;//加速度
    private String vecAy;
    private String vecAz;
    private boolean isLocked;
    private  double pitchX;
    private  double yawY;
    private  double rollZ;



    public static final Deserializer<BaseParticleType> DESERIALIZER = new Deserializer<>() {
        @Override
        public @NotNull BaseParticleType fromCommand(@NotNull ParticleType<BaseParticleType> pParticleType, StringReader pReader) throws CommandSyntaxException {
            final int MIN_COLOUR = 0;
            final int MAX_COLOUR = 255;
            pReader.expect(' '); // 处理空格
            double speedX = pReader.readDouble(); // 读取第一个0
            pReader.expect(' '); // 处理空格
            double speedY = pReader.readDouble(); //....
            pReader.expect(' ');
            double speedZ = pReader.readDouble();
            pReader.expect(' ');
            int red = clamp(pReader.readInt(), MIN_COLOUR, MAX_COLOUR);
            pReader.expect(' ');
            int green = clamp(pReader.readInt(), MIN_COLOUR, MAX_COLOUR);
            pReader.expect(' ');
            int blue = clamp(pReader.readInt(), MIN_COLOUR, MAX_COLOUR);
            pReader.expect(' ');
            int alpha = clamp(pReader.readInt(), 1, MAX_COLOUR);
            pReader.expect(' ');
            float diameter = pReader.readFloat();
            pReader.expect(' ');
            int lifetime = pReader.readInt();
            String vecExpX = pReader.readString();
            pReader.expect(' ');
            String vecExpY = pReader.readString();
            pReader.expect(' ');
            String vecExpZ = pReader.readString();
            pReader.expect(' ');
            double ax = pReader.readDouble();
            pReader.expect(' ');
            double ay = pReader.readDouble();
            pReader.expect(' ');
            double az = pReader.readDouble();
            pReader.expect(' ');
            double centerX = pReader.readDouble();
            pReader.expect(' ');
            double centerY = pReader.readDouble();
            pReader.expect(' ');
            double centerZ = pReader.readDouble();
            pReader.expect(' ');
            pReader.expect(' ');
            int e1 = pReader.readInt();
            List<Integer> l1 = new ArrayList<>();
            l1.add(e1);
            int entityId = pReader.readInt();
            int fps = pReader.readInt();
            String dynamicExp = pReader.readString();


            return new BaseParticleType(new Vector3d(speedX, speedY, speedZ), new Vector4i(red, green, blue, alpha), diameter, lifetime, vecExpX, vecExpY, vecExpZ, ax, ay, az, centerX, centerY, centerZ, l1, entityId, fps, dynamicExp, 0, 0, 0,"","","",false,-1,-1,-1);
        }


        @Override
        public @NotNull BaseParticleType fromNetwork(@NotNull ParticleType<BaseParticleType> pParticleType, FriendlyByteBuf pBuffer) {
            final int MIN_COLOUR = 0;
            final int MAX_COLOUR = 255;
            double speedX = pBuffer.readDouble();
            double speedY = pBuffer.readDouble();
            double speedZ = pBuffer.readDouble();
            int red = clamp(pBuffer.readInt(), MIN_COLOUR, MAX_COLOUR);
            int green = clamp(pBuffer.readInt(), MIN_COLOUR, MAX_COLOUR);
            int blue = clamp(pBuffer.readInt(), MIN_COLOUR, MAX_COLOUR);
            int alpha = clamp(pBuffer.readInt(), 1, MAX_COLOUR);
            float diameter = pBuffer.readFloat();
            int lifetime = pBuffer.readInt();
            String vecExpX = pBuffer.readUtf();
            String vecExpY = pBuffer.readUtf();
            String vecExpZ = pBuffer.readUtf();
            double ax = pBuffer.readDouble();
            double ay = pBuffer.readDouble();
            double az = pBuffer.readDouble();
            double centerX = pBuffer.readDouble();
            double centerY = pBuffer.readDouble();
            double centerZ = pBuffer.readDouble();
            List<Integer> entitiesID = pBuffer.readIntIdList();
            int entityId = pBuffer.readInt();
            int fps = pBuffer.readInt();
            String dynamicExp = pBuffer.readUtf();
            double rx = pBuffer.readDouble();
            double ry = pBuffer.readDouble();
            double rz = pBuffer.readDouble();
            String vecAx=pBuffer.readUtf();
            String vecAy=pBuffer.readUtf();
            String vecAz=pBuffer.readUtf();
            boolean isLocked=pBuffer.readBoolean();
            double pitchX=pBuffer.readDouble();
            double yawY=pBuffer.readDouble();
            double rollZ=pBuffer.readDouble();
            return new BaseParticleType(new Vector3d(speedX, speedY, speedZ), new Vector4i(red, green, blue, alpha), diameter, lifetime, vecExpX, vecExpY, vecExpZ, ax, ay, az, centerX, centerY, centerZ, entitiesID, entityId, fps, dynamicExp, rx, ry, rz,vecAx,vecAy,vecAz,isLocked,pitchX,yawY,rollZ);
        }
    };


    public BaseParticleType(Vector3d speed, Vector4i color, float diameter, int lifetime, String vecExpX, String vecExpY, String vecExpZ, double ax, double ay, double az, double centerX, double centerY, double centerZ, List<Integer> entitiesID, int entityID, int fps, String dynamicExp, double rx, double ry, double rz,String vecAx,String vecAy,String vecAz,boolean isLocked,double pitchX,double yawY,double rollZ) {

        super(true, DESERIALIZER);//控制粒子在人视线之外是否渲染(薛定谔的粒子
        this.speed = speed;
        this.color = color;
        this.diameter = diameter;
        this.lifetime = lifetime;
        this.vecExpX = vecExpX;
        this.vecExpY = vecExpY;
        this.vecExpZ = vecExpZ;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.entitiesID = entitiesID;
        this.entityID = entityID;
        this.fps = fps;
        this.dynamicExp = dynamicExp;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.vecAx=vecAx;
        this.vecAz=vecAz;
        this.vecAy=vecAy;
        this.isLocked=isLocked;
        this.pitchX=pitchX;
        this.yawY=yawY;
        this.rollZ=rollZ;

    }

    //ININTIAL
    public BaseParticleType() {
        super(false, DESERIALIZER);
        this.speed = new Vector3d(0, 0, 0);
        this.color = new Vector4i(100, 100, 100, 255);
        this.diameter = 1;
        this.lifetime = 60;
        this.vecExpX = "0";
        this.vecExpY = "0";
        this.vecExpZ = "0";
        this.vecAx = "0";
        this.vecAy= "0";
        this.vecAz = "0";
        this.ax = 0;
        this.ay = 0;
        this.az = 0;
        this.centerX = 0;
        this.centerY = 0;
        this.centerZ = 0;
        this.entitiesID = entitiesID != null ? entitiesID : new ArrayList<>();
        this.dynamicExp = "";
        this.isLocked=false;
        this.pitchX=-1;
        this.yawY=-1;
        this.rollZ=-1;
    }


    @Override
    public @NotNull BaseParticleType getType() {
        return ModParticleType.EXAMPLE_PARTICLE_TYPE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.speed.x);
        pBuffer.writeDouble(this.speed.y);
        pBuffer.writeDouble(this.speed.z);
        pBuffer.writeInt(this.color.x());
        pBuffer.writeInt(this.color.y());
        pBuffer.writeInt(this.color.z());
        pBuffer.writeInt(this.color.w());
        pBuffer.writeFloat(this.diameter);
        pBuffer.writeInt(this.lifetime);
        pBuffer.writeUtf(this.vecExpX);
        pBuffer.writeUtf(this.vecExpY);
        pBuffer.writeUtf(this.vecExpZ);
        pBuffer.writeDouble(this.centerX);
        pBuffer.writeDouble(this.centerY);
        pBuffer.writeDouble(this.centerZ);
        pBuffer.writeDouble(this.fps);
        pBuffer.writeUtf(this.dynamicExp);
        pBuffer.writeDouble(this.rx);
        pBuffer.writeDouble(this.ry);
        pBuffer.writeDouble(this.rz);
        pBuffer.writeUtf(this.vecAx);
        pBuffer.writeUtf(this.vecAy);
        pBuffer.writeUtf(this.vecAz);
        pBuffer.writeBoolean(this.isLocked);
        pBuffer.writeDouble(this.pitchX);
        pBuffer.writeDouble(this.yawY);
        pBuffer.writeDouble(this.rollZ);
    }


    @Override
    public @NotNull String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %d %d %d %d %.2f %.2f %.2f %d",//
                this.getType(), diameter, color.x(), color.y(), color.z(), color.w(), speed.x(), speed.y(), speed.z(), lifetime) + "...andMore";
    }

    @Override
    public @NotNull Codec<BaseParticleType> codec() {
        return Codec.unit(new BaseParticleType());
    }

    public BaseParticleType setDynamicExp(String dynamicExp) {
        String[] parts = dynamicExp.split(";");
        for (String part : parts) {
            // 去除前后空格
            String trimmedPart = part.trim();
            // 检查格式
            String regex = "^[^<\\-]+<-.*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(trimmedPart);
            if (!matcher.matches()) {
                // 如果有部分不符合格式，则抛出异常
                this.dynamicExp="t<-t";
                return this;
            }
        }
        this.dynamicExp = dynamicExp;

        return this;
    }
    public double[] getAngle(){
        return new  double[]{pitchX,yawY,rollZ};
    }
    public BaseParticleType setAngle( double pitchX,double yawY,double rollZ){
        this.pitchX=pitchX;
        this.yawY=yawY;
        this.rollZ=rollZ;
        return this;
    }

    public String getDynamicExp() {
        return this.dynamicExp;
    }


    public BaseParticleType setRotation(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        return this;
    }

    public BaseParticleType setColor(Vector4i color) {
        int r = clamp(color.x, 0, 255);
        int g = clamp(color.y, 0, 255);
        int b = clamp(color.z, 0, 255);
        int a = color.w;
        this.color = new Vector4i(r, g, b, a);
        return this;
    }
    private BaseParticleType setIsLocked(boolean isLocked){
        this.isLocked=isLocked;
        return this;
    }
    public BaseParticleType setSpeed(Vector3d speed) {
        this.speed = speed;
        return this;
    }

    public BaseParticleType setDiameter(float diameter) {
        this.diameter = diameter;
        return this;
    }

    public BaseParticleType setLifetime(int lifetime) {
        if (lifetime < 0) {
            throw new IllegalArgumentException("生命时长非负");
        }
        this.lifetime = lifetime;
        return this;
    }

    public BaseParticleType setEntitiesID(List<Integer> entitiesID) {
        this.entitiesID = entitiesID.stream().toList(); // 要+1才能用
        return this;
    }


    public BaseParticleType setVecExpX(String vecX) {
        this.vecExpX = vecX;
        return this;
    }

    public BaseParticleType setVecExpY(String vecY) {
        this.vecExpY = vecY;
        return this;
    }

    public BaseParticleType setVecExpZ(String vecZ) {
        this.vecExpZ = vecZ;
        return this;
    }

    public BaseParticleType setAx(double ax) {
        this.ax = ax;
        return this;
    }

    public BaseParticleType setAy(double ay) {
        this.ay = ay;
        return this;
    }

    public BaseParticleType setAz(double az) {
        this.az = az;
        return this;
    }

    public BaseParticleType setCenter(double centerX, double centerY, double centerZ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        return this;
    }

    public BaseParticleType setFps(float fps) {
        this.fps = fps;
        return this;
    }
    public void setAExp(String vecAx, String vecAy, String vecAz){
        this.vecAx=vecAx;
        this.vecAy=vecAy;
        this.vecAz=vecAz;
    }

    public String getVecAx(){
        return this.vecAx;
    }
    public String getVecAy(){
        return this.vecAy;
    }
    public String getVecAz(){
        return this.vecAz;
    }
    public boolean getIsLocked(){return this.isLocked;}



    public double getRotationX() {
        return this.rx;
    }

    public double getRotationZ() {
        return this.rz;
    }

    public double getRotationY() {
        return this.ry;
    }

    public float getFps() {
        return this.fps;
    }

    public Vector3d getSpeed() {
        return speed;
    }

    public Vector4i getColor() {
        return color;
    }

    public float getDiameter() {
        return diameter;
    }

    public int getLifetime() {
        return lifetime;
    }

    public List<Double> getCenter() {
        return Arrays.asList(centerX, centerY, centerZ);

    }

    public String getVecExpX() {
        return vecExpX;
    }

    public String getVecExpY() {
        return vecExpY;
    }

    public String getVecExpZ() {
        return vecExpZ;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }

    public double getAz() {
        return az;
    }

    public List<Integer> getEntitiesID() {
        return entitiesID;
    }

    public Integer getEntityID() {
        return this.entityID;
    }


}

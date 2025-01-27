package com.example.particlecomplex.entities.test;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.particles.base.ParticleAreaSpawner;
import com.example.particlecomplex.particles.custom.END_ROD;
import com.example.particlecomplex.particles.custom.EXPLOSION;
import com.example.particlecomplex.utils.entity_utils.EntityGetter;
import com.example.particlecomplex.utils.task_scheduler_utils.ParticleScheduler;
import com.example.particlecomplex.utils.task_scheduler_utils.tasks.createByCurveTask;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4i;

import java.util.List;
import java.util.Random;

public class AmmoEntity extends Projectile {
    private final int radius=12;
    private final ParticleScheduler scheduler =new ParticleScheduler(this.level());


    private Entity entity;
    private final long start_time=this.level().getGameTime();
    private int N=5;

    public AmmoEntity(EntityType<? extends AmmoEntity> entityType, Level level, Entity startEntity) {
        super(entityType, level);
        this.noCulling = true;
        this.entity =startEntity;
    }

    public AmmoEntity(EntityType<? extends AmmoEntity> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
        this.entity =this;
    }

    @Override
    public void tick() {
        super.tick();
        if((start_time-level().getGameTime())%20==0){
            N--;
            if (!this.level().isClientSide) {
                this.change_target(); // 继续运动
            }else{
                this.change_target();
                spawnParticlesCurve();
                spawnParticlesCircle();
            }
        }
        if (!this.level().isClientSide) {
            this.move_s(); // 继续运动
        }else{
            this.level().addParticle(ParticleTypes.END_ROD,true,this.getX(),this.getY(),this.getZ(),0,0,0);
        }

        if(N<0){
            this.discard();
        }
    }
    private void createParticleBall(Level level, float x_f, float y_f, float z_f,int radius) {
        BaseParticleType particleType = new END_ROD();
        particleType
                .setDynamicExp("w<-255*0.5*(1-cos((2*pi*t)/lifetime))")
                .setLifetime(40)
                .setRotation(0, 4, 4)
                .setColor(new Vector4i(100, 100, 100, 500))
                .setDiameter(0.4f)// 设置缩放
                .setCenter(x_f, y_f, z_f)// 设置中心坐标,x,y,z,e?x,e?y,e?z返回的都是实际实体||粒子坐标-中心坐标,也就是相对位置
                .setFps(35);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(level, particleType, -radius, radius, 0.4f);
        areaParticle.createByPositionEquation(x_f,y_f,z_f,"x^2+y^2+z^2-"+ radius * radius,0.5);
    }

    private void move_s() {
        double x_o=this.getX();
        double y_o=this.getY();
        double z_o=this.getZ();
        double ex=entity.getX();
        double ey=entity.getY();
        double ez=entity.getZ();
        double value=1;
        Vec3 delta=new Vec3(ex-x_o,ey-y_o,ez-z_o).normalize().scale(value);
        this.setPos(new Vec3(x_o,y_o,z_o).add(delta));
    }

    private void change_target(){
        this.entity.hurt(this.damageSources().magic(),5);
        List<Entity> entities= EntityGetter.getEntitiesInCircle(level(),entity.getX(),entity.getY(),entity.getZ(),radius,true);
        entities.remove(this);
        entities.remove(this.entity);
        if(!entities.isEmpty()){
            this.entity= entities.get(0);}
        else {
            this.discard();
        }

    }
    private void spawnParticlesCircle(){
        createParticleBall(level(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(),radius);
    }
    private void spawnParticlesCurve(){
        BaseParticleType particleType = new EXPLOSION();
        particleType
                .setDynamicExp("w<-255*0.5*(1-cos((2*pi*t)/lifetime));r<-255*t/lifetime;g<-255-(255*t)/lifetime;b<-0")
                .setLifetime(50)
                .setRotation(0, 0, 0)
                .setColor(new Vector4i(100, 100, 100, 500))
                .setDiameter(0.7f)// 设置缩放
                .setCenter( (float) entity.getX(), (float) entity.getY(), (float) entity.getZ())// 设置中心坐标,x,y,z,e?x,e?y,e?z返回的都是实际实体||粒子坐标-中心坐标,也就是相对位置
                .setFps(250);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(level(), particleType, 3, 3, 0.3f);
        double[][] a={{this.getX(),this.getY(),this.getZ()},
                {this.getX()+ new Random().nextDouble(radius),
                this.getY()+ new Random().nextDouble(radius),
                this.getZ()+ new Random().nextDouble(radius)},
                {this.entity.getX(),this.entity.getY(),this.entity.getZ()}};
        System.out.println(this.getX());
        System.out.println(this.getY());
        System.out.println(this.entity.getX());
        System.out.println(this.entity.getY());
        scheduler.addTask(new createByCurveTask(areaParticle,0.05,a,"BezierCurve",1));
        scheduler.addTask(new createByCurveTask(areaParticle,0.05,a,"BezierCurve",20));
    }


    @Override
    protected void defineSynchedData() {
        // 不需要同步额外的数据
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        // 不需要额外的保存数据
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        // 不需要额外的保存数据
    }
}

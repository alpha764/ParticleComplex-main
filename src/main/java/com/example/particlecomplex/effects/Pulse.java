package com.example.particlecomplex.effects;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.particles.base.ParticleAreaSpawner;
import com.example.particlecomplex.particles.custom.END_ROD;
import com.example.particlecomplex.registry.ModParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector4i;

import java.util.List;
import java.util.Objects;
import java.util.Random;
//TODO 做好AreaSpawner的ServerLevel的sendParticle方法,effect里最好不要塞客户端方法
public class Pulse extends MobEffect {

    public Pulse(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    static void createParticleBeam(Level level, float x_f, float y_f, float z_f) {
        BaseParticleType particleType2 = ModParticleType.SONIC_BOOM.get();
        particleType2
                .setRotation(0, 0, 0)
                .setColor(new Vector4i(100, 100, 100, 500)) // 设置速度
                .setDiameter(1f) // 设置缩放
                .setCenter(x_f, y_f, z_f) // 设置中心坐标
                .setFps(80)
                .setDynamicExp("centerX<-e0x;centerY<-e0y;centerZ<-e0z");

        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(level, particleType2, -5, 8, (float) (0.5));
        areaParticle.setPositionExpression("(random()-0.5)*6", "-10+t*3", "(random()-0.5)*6");
        areaParticle.createByPositionExpression(x_f, y_f, z_f);
    }
    static void createRandomLine(Level level, float x_f, float y_f, float z_f) {
        BaseParticleType particleType2 = ModParticleType.DRAGON_BREATH.get();
        particleType2
                .setRotation(0, 0, 0)
                .setColor(new Vector4i(140, 0, 192, 500)) // 设置速度
                .setDiameter(1f) // 设置缩放
                .setCenter(x_f, y_f, z_f) // 设置中心坐标
                .setFps(20)
                .setDynamicExp("w <- threshold(255 * (1 - (t / lifetime)^3)-10)");


        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(level, particleType2, 0, 30, (float) (1));
        double times=3;

        for (int i = 0; i < 2; i++) {
            double a=new Random().nextDouble()*times;
            double b=new Random().nextDouble()*times;
            double c=new Random().nextDouble()*times;
            areaParticle.setPositionExpression(a+" * t",b+" * t",c+" * t");
            areaParticle.createByPositionExpression(x_f,y_f,z_f);
        }
    }

    static void createRandomSquare(Level level, float x_f, float y_f, float z_f) {
        BaseParticleType particleType = new END_ROD();
        particleType
                .setLifetime(50)
                .setColor(new Vector4i(100, 100, 100, 500))
                .setDiameter(0.4f) // 设置缩放
                .setCenter(x_f, y_f, z_f) // 设置中心坐标
                .setRotation(0, 0, 0)
                .setFps(200)
                .setVecExpX("(sRandom-0.5)*((t)/lifetime)*cos((x/(t+1))*2*pi)*4")
                .setVecExpY("(sRandom-0.5)*((t)/lifetime)*sin((y/(t+1))*2*pi)*4")
                .setVecExpZ("(sRandom-0.5)*((t)/lifetime)*cos((z/(t+1))*2*pi)*4")
                .setDynamicExp("w <- threshold(255 * (1 - (t / lifetime)^3)-30)"); // 渐隐
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(level, particleType, -1, 1, (float) (1));
        areaParticle.setPolarPositionExpression(String.valueOf(0), "0", "0");
        for (int i = 0; i < 20; i++) {
            float randomX = new Random().nextFloat() - 0.5f;
            float randomZ = new Random().nextFloat() - 0.5f;
            int radius = 10;
            areaParticle.createByPolarPositionExpression(x_f + randomX * radius, y_f, z_f + randomZ * radius);
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!entity.level().isClientSide) {
            createRandomSquare(entity.level(), (float) x, (float) y, (float) z);
            if (amplifier >= 5) {
                createRandomLine(entity.level(), (float) x, (float) y, (float) z);
            }
        }

        // 获取周围的所有实体
        AABB area = new AABB(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5);
        entity.hurt(entity.damageSources().magic(), 0.5f);

        if (amplifier >= 5) {
            entity.hurt(entity.damageSources().magic(), 30);
            entity.removeEffect(this);
        }

        // 获取范围内的实体
        List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != entity && !(e.hasEffect(this)));

        // 选择一个随机实体
        if (!nearbyEntities.isEmpty()) {
            LivingEntity target = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));

            // 获取当前实体的效果持续时间
            MobEffectInstance effectInstance = entity.getEffect(this);
            if (effectInstance == null) return;

            int initialDuration = effectInstance.getDuration();
            int initAmplifier = effectInstance.getAmplifier();

            // 每次传染时减少 4 秒的持续时间
            int duration = Math.max(initialDuration - 60, 0); // 确保持续时间不会变为负数
            amplifier = initAmplifier + 1;

            // 更新当前实体的持续时间
            entity.removeEffect(this);
            entity.addEffect(new MobEffectInstance(this, duration, amplifier));

            // 给选中的目标应用新的效果
            target.removeEffect(this);
            target.addEffect(new MobEffectInstance(this, duration, amplifier)); // 传染时提高目标的药水等级
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每 20 tick 执行一次
        return (duration-amplifier*2) % 20 == 0;
    }
}

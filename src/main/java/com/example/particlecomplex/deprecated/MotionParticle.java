package com.example.particlecomplex.deprecated;

import com.example.particlecomplex.utils.expression_utils.Expression;
import com.example.particlecomplex.particles.base.BaseParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * @deprecated ParticleEngine直接调用存在问题,
 * 建议使用 {@link BaseParticle} 代替。
 */
//@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
@Deprecated
public class MotionParticle {

    private static final List<MotionParticle> activeParticles = new CopyOnWriteArrayList<>();
    private float a_x;
    private float a_y;
    private float a_z;
    private String statue;
    private long startTime;
    private Particle particle;
    private float init_x;
    private float init_y;
    private float init_z;
    private Expression xSpeedExpression;
    private Expression ySpeedExpression;
    private Expression zSpeedExpression;
    private final ParticleEngine engine = Minecraft.getInstance().particleEngine;
    private float vx;
    private float vy;
    private float vz;

    public MotionParticle() {
        this.particle = null;
        this.startTime = 0;
        this.init_x = 0;
        this.init_y = 0;
        this.init_z = 0;
        this.a_x = 0;
        this.a_y = 0;
        this.a_z = 0;
        this.vx = 0;
        this.vy = 0;
        this.vz = 0;
        this.statue = "";
    }

    // 通过表达式控制粒子运动速度
    public static MotionParticle createMotionParticleBySpeedExpression(ParticleOptions type, float x, float y, float z,
                                                                       Expression xSpeedExpression, Expression ySpeedExpression, Expression zSpeedExpression) {
        MotionParticle motionParticle = new MotionParticle();
        motionParticle.statue = "expression";
        motionParticle.create_particle(type, x, y, z);
        motionParticle.setXSpeedExpression(xSpeedExpression);
        motionParticle.setYSpeedExpression(ySpeedExpression);
        motionParticle.setZSpeedExpression(zSpeedExpression);
        activeParticles.add(motionParticle);
        return motionParticle;
    }

    // 通过加速度与速度共同控制运动
    public static MotionParticle createMotionParticleByAcceleration(ParticleOptions type, float x, float y, float z, float vx, float vy, float vz, float ax, float ay, float az) {
        MotionParticle motionParticle = new MotionParticle();
        motionParticle.statue = "acceleration";
        motionParticle.create_particle(type, x, y, z);
        motionParticle.setSpeed(vx, vy, vz);
        motionParticle.setAcceleration(ax, ay, az);
        activeParticles.add(motionParticle);
        return motionParticle;
    }

    // 通过加速度控制运动（速度默认为0）
    public static MotionParticle createMotionParticleByAcceleration(ParticleOptions type, float x, float y, float z, float ax, float ay, float az) {
        return createMotionParticleByAcceleration(type, x, y, z, 0, 0, 0, ax, ay, az);
    }

    // 创建粒子
    private void create_particle(ParticleOptions type, float x, float y, float z) {
        this.particle = engine.createParticle(type, x, y, z, 0, 0, 0);
        this.startTime = getTime();
        this.init_x = x;
        this.init_y = y;
        this.init_z = z;
    }

    public void setSpeed(float vx, float vy, float vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.particle.setParticleSpeed(vx, vy, vz);
    }

    public void setAcceleration(float ax, float ay, float az) {
        a_x = ax;
        a_y = ay;
        a_z = az;
    }

    private long getTime() {
        if (Minecraft.getInstance().level != null) {
            return Minecraft.getInstance().level.getGameTime();
        }
        return 0;
    }

    public void setXSpeedExpression(Expression expression) {
        this.xSpeedExpression = expression;
    }

    public void setYSpeedExpression(Expression expression) {
        this.ySpeedExpression = expression;
    }

    public void setZSpeedExpression(Expression expression) {
        this.zSpeedExpression = expression;
    }

    public long getSurviveTime() {
        return getTime() - this.startTime;
    }

    private float get_relative_X() {
        return (float) this.particle.getPos().x - init_x;
    }

    private float get_relative_Y() {
        return (float) this.particle.getPos().y - init_y;
    }

    private float get_relative_Z() {
        return (float) this.particle.getPos().z - init_z;
    }

    // 更新粒子速度
    private void update_by_expression() {
        if (this.particle != null && this.particle.isAlive()) {  // 检查粒子是否已被销毁
            long currentTime = getSurviveTime();
            float r_x = get_relative_X();
            float r_y = get_relative_Y();
            float r_z = get_relative_Z();

            // 分别计算 x、y、z 方向的速度
            float vx = xSpeedExpression.apply(currentTime, r_x, r_y, r_z);
            float vy = ySpeedExpression.apply(currentTime, r_x, r_y, r_z);
            float vz = zSpeedExpression.apply(currentTime, r_x, r_y, r_z);

            setSpeed(vx, vy, vz);
        } else {
            this.particle = null;
        }
    }

    private void update_by_acceleration() {
        if (this.particle != null && this.particle.isAlive()) {  // 检查粒子是否已被销毁
            setSpeed(this.vx + a_x, this.vy + a_y, this.vz + a_z);
        } else {
            this.particle = null;
        }
    }

    public boolean isFinished() {
        return this.particle == null || !this.particle.isAlive();
    }

    public void setParticleLifetime(int lifetime) {
        this.particle.setLifetime(lifetime);
    }

    // 在每个游戏刻更新所有活动粒子
//    @SubscribeEvent
//    public static void onClientTick(TickEvent.ClientTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//
//
////            ExampleMod.LOGGER.info("pSIZE:"+String.valueOf(activeParticles.size()));
////            ExampleMod.LOGGER.info(Minecraft.getInstance().particleEngine.countParticles());
//
//
//            for (MotionParticle particle : activeParticles) {
//                if (Objects.equals(particle.statue, "expression")) {
//                    particle.update_by_expression();
//                } else {
//                    particle.update_by_acceleration();
//                }
//                if (particle.isFinished()||(particle.getSurviveTime()-particle.particle.getLifetime()>0)) {
//                    activeParticles.remove(particle);
////                    if(particle.particle.isAlive()){particle.particle.remove();}
//                }
//            }
//        }
//    }
}

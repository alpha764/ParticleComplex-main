package com.example.particlecomplex.deprecated;

import com.example.particlecomplex.utils.expression_utils.EquationSolver;
import com.example.particlecomplex.utils.expression_utils.Expression;
import com.example.particlecomplex.particles.base.ParticleAreaSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;

import java.util.List;
import java.util.Objects;
/**
 * @deprecated ParticleEngine的相应方法导致全局粒子渲染失效,
 * 建议使用 {@link ParticleAreaSpawner} 代替。
 */
@Deprecated
public class AreaParticle {
    private final ParticleOptions type;
    private final float start;
    private final float end;
    private final float step;
    private double tolerance;
    private float ax;
    private float ay;
    private float az;
    private float vx;
    private float vy;
    private float vz;
    private  String speedStatue;
    private int lifetime;

    private Expression xPositionExpression;
    private Expression yPositionExpression;
    private Expression xSpeedExpression;
    private Expression ySpeedExpression;
    private Expression zSpeedExpression;
    private Expression zPositionExpression;
    private String equation;

    public AreaParticle(ParticleOptions type,float start,float end,float step) {
        this.type=type;
        this.start=start;
        this.end=end;
        this.step=step;
        this.lifetime=40;
        this.speedStatue="expression";
        this.ax=0;
        this.ay=0;
        this.az=0;
        this.vx=0;
        this.vy=0;
        this.vz=0;
        this.tolerance=0.01;
    }
    public void setPositionExpression(Expression expressionX,Expression expressionY,Expression expressionZ) {
        this.xPositionExpression = expressionX;
        this.yPositionExpression=expressionY;
        this.zPositionExpression=expressionZ;
    }
    public void setPositionEquation(String Equation,double tolerance) {
        this.equation=Equation;
        this.tolerance=tolerance;

    }
    public void setSpeedExpression(Expression expressionX,Expression expressionY,Expression expressionZ) {
        this.xSpeedExpression = expressionX;
        this.ySpeedExpression=expressionY;
        this.zSpeedExpression=expressionZ;
        this.speedStatue="expression";
    }
    public void setSpeedAcceleration(float ax, float ay, float az, float vx, float vy, float vz) {
        this.speedStatue="acceleration";
        this.ax=ax;
        this.ay=ay;
        this.az=az;
        this.vx=vx;
        this.vy=vy;
        this.vz=vz;
    }
    public void setSpeedAcceleration(float ax,float ay,float az) {
        this.speedStatue="acceleration";
        this.ax=ax;
        this.ay=ay;
        this.az=az;
    }
    public void setLifeTime(int lifetime) {
        this.lifetime=lifetime;
    }

    public void createByPositionExpression(float o_x,float o_y,float o_z){
        for(float i=this.start;i<=end;i+=step){
            float offset_x=xPositionExpression.apply(i,1,1,1);
            float offset_y=yPositionExpression.apply(i,1,1,1);
            float offset_z=zPositionExpression.apply(i,1,1,1);

            if(Objects.equals(this.speedStatue, "expression")){
            MotionParticle m=MotionParticle.createMotionParticleBySpeedExpression(type,
                    o_x+offset_x,o_y+offset_y,o_z+offset_z,
                    this.xSpeedExpression,this.ySpeedExpression,this.zSpeedExpression);
                     m.setParticleLifetime(this.lifetime);}

            if(Objects.equals("acceleration",this.speedStatue)){
                MotionParticle m=MotionParticle.createMotionParticleByAcceleration(type,
                        o_x+offset_x,o_y+offset_y,o_z+offset_z,
                        this.vx,this.vy,this.vz,this.ax,this.ay,this.az);
                m.setParticleLifetime(this.lifetime);}

            }
        }
    public void createByPositionEquation(float o_x,float o_y,float o_z){
        Minecraft.getInstance().getSplashManager();
        EquationSolver solver = new EquationSolver(this.equation, this.tolerance);
        List<double[]> points = solver.solve(this.start,this.end,this.step);
        for(double[] point: points){
            float offset_x = (float) point[0];
            float offset_y = (float) point[1];  // 修正错误
            float offset_z = (float) point[2];  // 修正错误
            if(Objects.equals(this.speedStatue, "expression")){
                MotionParticle m = MotionParticle.createMotionParticleBySpeedExpression(
                        type, o_x + offset_x, o_y + offset_y, o_z + offset_z,
                        this.xSpeedExpression, this.ySpeedExpression, this.zSpeedExpression);
                m.setParticleLifetime(this.lifetime);
            }
            if(Objects.equals("acceleration", this.speedStatue)){
                MotionParticle m = MotionParticle.createMotionParticleByAcceleration(
                        type, o_x + offset_x, o_y + offset_y, o_z + offset_z,
                        this.vx, this.vy, this.vz, this.ax, this.ay, this.az);
                m.setParticleLifetime(this.lifetime);
            }
        }
    }



    }



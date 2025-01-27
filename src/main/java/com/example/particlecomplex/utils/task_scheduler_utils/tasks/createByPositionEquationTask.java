package com.example.particlecomplex.utils.task_scheduler_utils.tasks;

import com.example.particlecomplex.utils.task_scheduler_utils.Task;
import com.example.particlecomplex.particles.base.ParticleAreaSpawner;

// 示例 1：没有参数的简单任务
public class createByPositionEquationTask extends Task {
    private final float o_x, o_y, o_z;
    private final double tolerance;
    private final String Equation;

    private final ParticleAreaSpawner areaSpawner;

    public createByPositionEquationTask(ParticleAreaSpawner areaSpawner,float o_x, float o_y, float o_z, String Equation, double tolerance,int delay) {
        super(delay);
        this.o_x=o_x;
        this.o_y=o_y;
        this.o_z=o_z;
        this.areaSpawner=areaSpawner;
        this.Equation=Equation;
        this.tolerance=tolerance;
    }

    @Override
    public void execute() {
        this.areaSpawner.createByPositionEquation(o_x,o_y,o_z,Equation,tolerance);
    }
}

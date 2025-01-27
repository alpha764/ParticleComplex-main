package com.example.particlecomplex.utils.task_scheduler_utils.tasks;

import com.example.particlecomplex.utils.task_scheduler_utils.Task;

import com.example.particlecomplex.particles.base.ParticleAreaSpawner;

public class createByPolarPositionExpressionTask extends Task {

    private final float x, y, z;

    private final ParticleAreaSpawner areaSpawner;


    public createByPolarPositionExpressionTask(ParticleAreaSpawner areaSpawner, float x, float y, float z, int delay) {
        super(delay);
        this.x = x;
        this.y = y;
        this.z = z;
        this.areaSpawner=areaSpawner;
    }

    @Override
    public void execute() {
        this.areaSpawner.createByPolarPositionExpression(x, y, z);
    }
}

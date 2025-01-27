package com.example.particlecomplex.utils.task_scheduler_utils.tasks;

import com.example.particlecomplex.utils.task_scheduler_utils.Task;

import com.example.particlecomplex.particles.base.ParticleAreaSpawner;

public class createByCurveTask extends Task {



    private final ParticleAreaSpawner areaSpawner;
    private final double[][] controlPoints;
    private final String CurveType;
    private final double step;


    public createByCurveTask(ParticleAreaSpawner areaSpawner,double step ,double[][] controlPoints, String CurveType, int delay) {
        super(delay);
        this.areaSpawner=areaSpawner;
        this.controlPoints=controlPoints;
        this.CurveType=CurveType;
        this.step=step;
    }

    @Override
    public void execute() {
        this.areaSpawner.createByCurve(step,controlPoints,CurveType);
    }
}

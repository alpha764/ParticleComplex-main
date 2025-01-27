package com.example.particlecomplex.utils.task_scheduler_utils.tasks;

import com.example.particlecomplex.utils.task_scheduler_utils.Task;

import com.example.particlecomplex.particles.base.ParticleAreaSpawner;

public class createByConnectedCurveTask extends Task {



    private final ParticleAreaSpawner areaSpawner;
    private final double[][] controlPoints;
    private final String CurveType;
    private final double step;
    private final int N;


    public createByConnectedCurveTask(ParticleAreaSpawner areaSpawner,double step ,double[][] controlPoints, String CurveType, int N,int delay) {
        super(delay);
        this.areaSpawner=areaSpawner;
        this.controlPoints=controlPoints;
        this.CurveType=CurveType;
        this.step=step;
        this.N=N;
    }

    @Override
    public void execute() {
        this.areaSpawner.createByConnectedCurve(step,controlPoints,CurveType,N);
    }
}

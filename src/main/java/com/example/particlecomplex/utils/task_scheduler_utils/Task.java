package com.example.particlecomplex.utils.task_scheduler_utils;

public abstract class Task {
    protected int delay; // 延迟执行的时间

    public Task(int delay) {
        this.delay = delay;
    }

    // 每个具体任务都要实现的执行方法
    public abstract void execute();
}

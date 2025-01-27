package com.example.particlecomplex.utils.task_scheduler_utils.tasks;

import com.example.particlecomplex.particles.base.ParticleAreaSpawner;
import com.example.particlecomplex.utils.task_scheduler_utils.Task;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class addEffectTask extends Task {


    private final LivingEntity entity;
    private final MobEffect effect;
    private final int amplifier;
    private final int duration;

    public addEffectTask(LivingEntity entity, MobEffect effect, int duration, int amplifier , int delay) {
        super(delay);
        this.entity=entity;
        this.effect=effect;
        this.duration=duration;
        this.amplifier=amplifier;

    }

    @Override
    public void execute() {
        MobEffectInstance instance= new MobEffectInstance(effect,duration,amplifier);
        entity.addEffect(instance);
    }
}

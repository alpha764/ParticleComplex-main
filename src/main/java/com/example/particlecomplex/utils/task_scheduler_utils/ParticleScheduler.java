package com.example.particlecomplex.utils.task_scheduler_utils;

import com.example.particlecomplex.ExampleMod;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ParticleScheduler {
    private static final List<Task> tasks = new ArrayList<>();
    private static final List<Long> start_times = new ArrayList<>();
    private static Level level ;

    public ParticleScheduler(Level level) {
        ParticleScheduler.level = level;
    }

    public void addTask(Task task) {
        tasks.add(task);
        long start_time = level.getGameTime();
        start_times.add(start_time);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Task> taskIterator = tasks.iterator();
            Iterator<Long> timeIterator = start_times.iterator();
            while (taskIterator.hasNext() && timeIterator.hasNext()) {
                long current_time = level.getGameTime();
                Task task = taskIterator.next();
                long start_time = timeIterator.next();
                if (current_time - start_time - task.delay > 0) {
                    task.execute();
                    taskIterator.remove();
                    timeIterator.remove();
                }
        }

    }}

}

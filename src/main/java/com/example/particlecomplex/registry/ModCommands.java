package com.example.particlecomplex.registry;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.commands.ParticleComplex;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID,bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event){
        ParticleComplex.register(event.getDispatcher());

    }

}

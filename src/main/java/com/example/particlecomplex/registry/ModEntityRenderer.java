package com.example.particlecomplex.registry;


import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.entities.base.BaseEntityRenderer;
import com.example.particlecomplex.entities.test.AmmoRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityRenderer {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        EntityRenderers.register(ModEntities.AMMO.get(), AmmoRenderer::new);
        EntityRenderers.register(ModEntities.BASE.get(), BaseEntityRenderer::new);



    }
}
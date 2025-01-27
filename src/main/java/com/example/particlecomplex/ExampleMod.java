package com.example.particlecomplex;
import com.example.particlecomplex.registry.ModParticleType;
import com.example.particlecomplex.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;



@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ExampleMod
{

    public static final String MODID = "particlecomplex";
    public static final Logger LOGGER =LogUtils.getLogger();
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =DeferredRegister.create(Registries.CREATIVE_MODE_TAB,MODID);
    private static final DeferredRegister<Item> ITEMS =DeferredRegister.create(ForgeRegistries.ITEMS,MODID);
    private static final DeferredRegister<Block> BLOCKS =DeferredRegister.create(ForgeRegistries.BLOCKS,MODID);
    public ExampleMod(){
        var bus= FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.__init__();
        ModParticleType.register(bus);
        bus.addListener(this::onClientSetup);
        CREATIVE_MODE_TABS.register(bus);
    }
    private void onClientSetup(FMLClientSetupEvent event) {
    }




}

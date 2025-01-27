package com.example.particlecomplex.registry;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.entities.test.AmmoEntity;

import com.example.particlecomplex.entities.base.BaseEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);

    public static final RegistryObject<EntityType<AmmoEntity>> AMMO =
            ENTITY_TYPES.register("ammo", () -> EntityType.Builder.<AmmoEntity>of(AmmoEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("ammo"));

    public static final RegistryObject<EntityType<BaseEntity>> BASE =
            ENTITY_TYPES.register("base", () -> EntityType.Builder.<BaseEntity>of(BaseEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("base"));


    public static void __init__() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
        ExampleMod.LOGGER.debug("ENTITIES initialized");
    }
}

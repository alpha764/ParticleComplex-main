package com.example.particlecomplex.registry;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.particles.base.BaseParticleType;
import com.example.particlecomplex.particles.custom.SonicBombParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModParticleType {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ExampleMod.MODID);

    
    public static <T extends ParticleType<?>> RegistryObject<T> register(String name, Supplier<T> particleType){
        return PARTICLE_TYPES.register(name, particleType);
    }

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
    public static final Supplier<BaseParticleType> EXAMPLE_PARTICLE_TYPE = register("example_particle_type", BaseParticleType::new);
    public static final Supplier<BaseParticleType> SONIC_BOOM = register("sonic_boom", SonicBombParticleType::new);
    public static final Supplier<BaseParticleType> DOLPHIN = register("dolphin" , com.example.particlecomplex.particles.custom.DOLPHIN::new);
    public static final Supplier<BaseParticleType> AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect" , com.example.particlecomplex.particles.custom.AMBIENT_ENTITY_EFFECT::new);
    public static final Supplier<BaseParticleType> ANGRY_VILLAGER = register("angry_villager" , com.example.particlecomplex.particles.custom.ANGRY_VILLAGER::new);
    public static final Supplier<BaseParticleType> ASH = register("ash" , com.example.particlecomplex.particles.custom.ASH::new);
    public static final Supplier<BaseParticleType> BUBBLE = register("bubble" , com.example.particlecomplex.particles.custom.BUBBLE::new);
    public static final Supplier<BaseParticleType> BUBBLE_COLUMN_UP = register("bubble_column_up" , com.example.particlecomplex.particles.custom.BUBBLE_COLUMN_UP::new);
    public static final Supplier<BaseParticleType> BUBBLE_POP = register("bubble_pop" , com.example.particlecomplex.particles.custom.BUBBLE_POP::new);
    public static final Supplier<BaseParticleType> CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke" , com.example.particlecomplex.particles.custom.CAMPFIRE_COSY_SMOKE::new);
    public static final Supplier<BaseParticleType> CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke" , com.example.particlecomplex.particles.custom.CAMPFIRE_SIGNAL_SMOKE::new);
    public static final Supplier<BaseParticleType> CHERRY_LEAVES = register("cherry_leaves" , com.example.particlecomplex.particles.custom.CHERRY_LEAVES::new);
    public static final Supplier<BaseParticleType> CLOUD = register("cloud" , com.example.particlecomplex.particles.custom.CLOUD::new);
    public static final Supplier<BaseParticleType> COMPOSTER = register("composter" , com.example.particlecomplex.particles.custom.COMPOSTER::new);
    public static final Supplier<BaseParticleType> CRIMSON_SPORE = register("crimson_spore" , com.example.particlecomplex.particles.custom.CRIMSON_SPORE::new);
    public static final Supplier<BaseParticleType> CRIT = register("crit" , com.example.particlecomplex.particles.custom.CRIT::new);
    public static final Supplier<BaseParticleType> CURRENT_DOWN = register("current_down" , com.example.particlecomplex.particles.custom.CURRENT_DOWN::new);
    public static final Supplier<BaseParticleType> DAMAGE_INDICATOR = register("damage_indicator" , com.example.particlecomplex.particles.custom.DAMAGE_INDICATOR::new);
    public static final Supplier<BaseParticleType> DRAGON_BREATH = register("dragon_breath" , com.example.particlecomplex.particles.custom.DRAGON_BREATH::new);
    public static final Supplier<BaseParticleType> DRIPPING_DRIPSTONE_LAVA = register("dripping_dripstone_lava" , com.example.particlecomplex.particles.custom.DRIPPING_DRIPSTONE_LAVA::new);
    public static final Supplier<BaseParticleType> DRIPPING_DRIPSTONE_WATER = register("dripping_dripstone_water" , com.example.particlecomplex.particles.custom.DRIPPING_DRIPSTONE_WATER::new);
    public static final Supplier<BaseParticleType> DRIPPING_HONEY = register("dripping_honey" , com.example.particlecomplex.particles.custom.DRIPPING_HONEY::new);
    public static final Supplier<BaseParticleType> DRIPPING_LAVA = register("dripping_lava" , com.example.particlecomplex.particles.custom.DRIPPING_LAVA::new);
    public static final Supplier<BaseParticleType> DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear" , com.example.particlecomplex.particles.custom.DRIPPING_OBSIDIAN_TEAR::new);
    public static final Supplier<BaseParticleType> DRIPPING_WATER = register("dripping_water" , com.example.particlecomplex.particles.custom.DRIPPING_WATER::new);
    public static final Supplier<BaseParticleType> DUST = register("dust" , com.example.particlecomplex.particles.custom.DUST::new);
    public static final Supplier<BaseParticleType> DUST_COLOR_TRANSITION = register("dust_color_transition" , com.example.particlecomplex.particles.custom.DUST_COLOR_TRANSITION::new);
    public static final Supplier<BaseParticleType> EFFECT = register("effect" , com.example.particlecomplex.particles.custom.EFFECT::new);
    public static final Supplier<BaseParticleType> EGG_CRACK = register("egg_crack" , com.example.particlecomplex.particles.custom.EGG_CRACK::new);
    public static final Supplier<BaseParticleType> ELECTRIC_SPARK = register("electric_spark" , com.example.particlecomplex.particles.custom.ELECTRIC_SPARK::new);
    public static final Supplier<BaseParticleType> ENCHANT = register("enchant" , com.example.particlecomplex.particles.custom.ENCHANT::new);
    public static final Supplier<BaseParticleType> ENCHANTED_HIT = register("enchanted_hit" , com.example.particlecomplex.particles.custom.ENCHANTED_HIT::new);
    public static final Supplier<BaseParticleType> END_ROD = register("end_rod" , com.example.particlecomplex.particles.custom.END_ROD::new);
    public static final Supplier<BaseParticleType> ENTITY_EFFECT = register("entity_effect" , com.example.particlecomplex.particles.custom.ENTITY_EFFECT::new);
    public static final Supplier<BaseParticleType> EXPLOSION = register("explosion" , com.example.particlecomplex.particles.custom.EXPLOSION::new);
    public static final Supplier<BaseParticleType> FALLING_DRIPSTONE_LAVA = register("falling_dripstone_lava" , com.example.particlecomplex.particles.custom.FALLING_DRIPSTONE_LAVA::new);
    public static final Supplier<BaseParticleType> FALLING_DRIPSTONE_WATER = register("falling_dripstone_water" , com.example.particlecomplex.particles.custom.FALLING_DRIPSTONE_WATER::new);
    public static final Supplier<BaseParticleType> FALLING_DUST = register("falling_dust" , com.example.particlecomplex.particles.custom.FALLING_DUST::new);
    public static final Supplier<BaseParticleType> FALLING_HONEY = register("falling_honey" , com.example.particlecomplex.particles.custom.FALLING_HONEY::new);
    public static final Supplier<BaseParticleType> FALLING_LAVA = register("falling_lava" , com.example.particlecomplex.particles.custom.FALLING_LAVA::new);
    public static final Supplier<BaseParticleType> FALLING_NECTAR = register("falling_nectar" , com.example.particlecomplex.particles.custom.FALLING_NECTAR::new);
    public static final Supplier<BaseParticleType> FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear" , com.example.particlecomplex.particles.custom.FALLING_OBSIDIAN_TEAR::new);
    public static final Supplier<BaseParticleType> FALLING_SPORE_BLOSSOM = register("falling_spore_blossom" , com.example.particlecomplex.particles.custom.FALLING_SPORE_BLOSSOM::new);
    public static final Supplier<BaseParticleType> FALLING_WATER = register("falling_water" , com.example.particlecomplex.particles.custom.FALLING_WATER::new);
    public static final Supplier<BaseParticleType> FIREWORK = register("firework" , com.example.particlecomplex.particles.custom.FIREWORK::new);
    public static final Supplier<BaseParticleType> FISHING = register("fishing" , com.example.particlecomplex.particles.custom.FISHING::new);
    public static final Supplier<BaseParticleType> FLAME = register("flame" , com.example.particlecomplex.particles.custom.FLAME::new);
    public static final Supplier<BaseParticleType> FLASH = register("flash" , com.example.particlecomplex.particles.custom.FLASH::new);
    public static final Supplier<BaseParticleType> GLOW = register("glow" , com.example.particlecomplex.particles.custom.GLOW::new);
    public static final Supplier<BaseParticleType> GLOW_SQUID_INK = register("glow_squid_ink" , com.example.particlecomplex.particles.custom.GLOW_SQUID_INK::new);
    public static final Supplier<BaseParticleType> HAPPY_VILLAGER = register("happy_villager" , com.example.particlecomplex.particles.custom.HAPPY_VILLAGER::new);
    public static final Supplier<BaseParticleType> HEART = register("heart" , com.example.particlecomplex.particles.custom.HEART::new);
    public static final Supplier<BaseParticleType> INSTANT_EFFECT = register("instant_effect" , com.example.particlecomplex.particles.custom.INSTANT_EFFECT::new);
    public static final Supplier<BaseParticleType> LANDING_HONEY = register("landing_honey" , com.example.particlecomplex.particles.custom.LANDING_HONEY::new);
    public static final Supplier<BaseParticleType> LANDING_LAVA = register("landing_lava" , com.example.particlecomplex.particles.custom.LANDING_LAVA::new);
    public static final Supplier<BaseParticleType> LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear" , com.example.particlecomplex.particles.custom.LANDING_OBSIDIAN_TEAR::new);
    public static final Supplier<BaseParticleType> LARGE_SMOKE = register("large_smoke" , com.example.particlecomplex.particles.custom.LARGE_SMOKE::new);
    public static final Supplier<BaseParticleType> LAVA = register("lava" , com.example.particlecomplex.particles.custom.LAVA::new);
    public static final Supplier<BaseParticleType> MYCELIUM = register("mycelium" , com.example.particlecomplex.particles.custom.MYCELIUM::new);
    public static final Supplier<BaseParticleType> NAUTILUS = register("nautilus" , com.example.particlecomplex.particles.custom.NAUTILUS::new);
    public static final Supplier<BaseParticleType> NOTE = register("note" , com.example.particlecomplex.particles.custom.NOTE::new);
    public static final Supplier<BaseParticleType> POOF = register("poof" , com.example.particlecomplex.particles.custom.POOF::new);
    public static final Supplier<BaseParticleType> PORTAL = register("portal" , com.example.particlecomplex.particles.custom.PORTAL::new);
    public static final Supplier<BaseParticleType> RAIN = register("rain" , com.example.particlecomplex.particles.custom.RAIN::new);
    public static final Supplier<BaseParticleType> REVERSE_PORTAL = register("reverse_portal" , com.example.particlecomplex.particles.custom.REVERSE_PORTAL::new);
    public static final Supplier<BaseParticleType> SCRAPE = register("scrape" , com.example.particlecomplex.particles.custom.SCRAPE::new);
    public static final Supplier<BaseParticleType> SCULK_CHARGE = register("sculk_charge" , com.example.particlecomplex.particles.custom.SCULK_CHARGE::new);
    public static final Supplier<BaseParticleType> SCULK_CHARGE_POP = register("sculk_charge_pop" , com.example.particlecomplex.particles.custom.SCULK_CHARGE_POP::new);
    public static final Supplier<BaseParticleType> SCULK_SOUL = register("sculk_soul" , com.example.particlecomplex.particles.custom.SCULK_SOUL::new);
    public static final Supplier<BaseParticleType> SHRIEK = register("shriek" , com.example.particlecomplex.particles.custom.SHRIEK::new);
    public static final Supplier<BaseParticleType> SMALL_FLAME = register("small_flame" , com.example.particlecomplex.particles.custom.SMALL_FLAME::new);
    public static final Supplier<BaseParticleType> SMOKE = register("smoke" , com.example.particlecomplex.particles.custom.SMOKE::new);
    public static final Supplier<BaseParticleType> SNEEZE = register("sneeze" , com.example.particlecomplex.particles.custom.SNEEZE::new);
    public static final Supplier<BaseParticleType> SNOWFLAKE = register("snowflake" , com.example.particlecomplex.particles.custom.SNOWFLAKE::new);
    public static final Supplier<BaseParticleType> SOUL = register("soul" , com.example.particlecomplex.particles.custom.SOUL::new);
    public static final Supplier<BaseParticleType> SOUL_FIRE_FLAME = register("soul_fire_flame" , com.example.particlecomplex.particles.custom.SOUL_FIRE_FLAME::new);
    public static final Supplier<BaseParticleType> SPIT = register("spit" , com.example.particlecomplex.particles.custom.SPIT::new);
    public static final Supplier<BaseParticleType> SPLASH = register("splash" , com.example.particlecomplex.particles.custom.SPLASH::new);
    public static final Supplier<BaseParticleType> SPORE_BLOSSOM_AIR = register("spore_blossom_air" , com.example.particlecomplex.particles.custom.SPORE_BLOSSOM_AIR::new);
    public static final Supplier<BaseParticleType> SQUID_INK = register("squid_ink" , com.example.particlecomplex.particles.custom.SQUID_INK::new);
    public static final Supplier<BaseParticleType> SWEEP_ATTACK = register("sweep_attack" , com.example.particlecomplex.particles.custom.SWEEP_ATTACK::new);
    public static final Supplier<BaseParticleType> TOTEM_OF_UNDYING = register("totem_of_undying" , com.example.particlecomplex.particles.custom.TOTEM_OF_UNDYING::new);
    public static final Supplier<BaseParticleType> UNDERWATER = register("underwater" , com.example.particlecomplex.particles.custom.UNDERWATER::new);
    public static final Supplier<BaseParticleType> VIBRATION = register("vibration" , com.example.particlecomplex.particles.custom.VIBRATION::new);
    public static final Supplier<BaseParticleType> WARPED_SPORE = register("warped_spore" , com.example.particlecomplex.particles.custom.WARPED_SPORE::new);
    public static final Supplier<BaseParticleType> WAX_OFF = register("wax_off" , com.example.particlecomplex.particles.custom.WAX_OFF::new);
    public static final Supplier<BaseParticleType> WAX_ON = register("wax_on" , com.example.particlecomplex.particles.custom.WAX_ON::new);
    public static final Supplier<BaseParticleType> WHITE_ASH = register("white_ash" , com.example.particlecomplex.particles.custom.WHITE_ASH::new);
    public static final Supplier<BaseParticleType> WITCH = register("witch" , com.example.particlecomplex.particles.custom.WITCH::new);
}

package com.example.particlecomplex.registry;

import com.example.particlecomplex.particles.base.BaseParticleProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleFactoryRegistry {
    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleType.EXAMPLE_PARTICLE_TYPE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SONIC_BOOM.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.AMBIENT_ENTITY_EFFECT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ANGRY_VILLAGER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ASH.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.BUBBLE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.BUBBLE_COLUMN_UP.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.BUBBLE_POP.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CAMPFIRE_COSY_SMOKE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CAMPFIRE_SIGNAL_SMOKE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CHERRY_LEAVES.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CLOUD.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.COMPOSTER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CRIMSON_SPORE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CRIT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.CURRENT_DOWN.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DAMAGE_INDICATOR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DOLPHIN.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRAGON_BREATH.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_DRIPSTONE_LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_DRIPSTONE_WATER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_HONEY.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_OBSIDIAN_TEAR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DRIPPING_WATER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DUST.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.DUST_COLOR_TRANSITION.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.EFFECT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.EGG_CRACK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ELECTRIC_SPARK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ENCHANT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ENCHANTED_HIT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.END_ROD.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.ENTITY_EFFECT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.EXPLOSION.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_DRIPSTONE_LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_DRIPSTONE_WATER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_DUST.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_HONEY.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_NECTAR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_OBSIDIAN_TEAR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_SPORE_BLOSSOM.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FALLING_WATER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FIREWORK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FISHING.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FLAME.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.FLASH.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.GLOW.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.GLOW_SQUID_INK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.HAPPY_VILLAGER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.HEART.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.INSTANT_EFFECT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.LANDING_HONEY.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.LANDING_LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.LANDING_OBSIDIAN_TEAR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.LARGE_SMOKE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.LAVA.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.MYCELIUM.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.NAUTILUS.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.NOTE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.POOF.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.PORTAL.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.RAIN.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.REVERSE_PORTAL.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SCRAPE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SCULK_CHARGE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SCULK_CHARGE_POP.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SCULK_SOUL.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SHRIEK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SMALL_FLAME.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SMOKE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SNEEZE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SNOWFLAKE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SOUL.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SOUL_FIRE_FLAME.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SPIT.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SPLASH.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SPORE_BLOSSOM_AIR.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SQUID_INK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.SWEEP_ATTACK.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.TOTEM_OF_UNDYING.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.UNDERWATER.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.VIBRATION.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.WARPED_SPORE.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.WAX_OFF.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.WAX_ON.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.WHITE_ASH.get(), BaseParticleProvider::new);
        event.registerSpriteSet(ModParticleType.WITCH.get(), BaseParticleProvider::new);
    }
}

package me.lily.bllry.mixins;

import me.lily.bllry.Bllry;
import me.lily.bllry.modules.impl.visuals.NoRenderModule;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> info) {
        if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).explosions.getValue() && parameters.getType() == ParticleTypes.EXPLOSION) {
            info.cancel();
        }
    }
}

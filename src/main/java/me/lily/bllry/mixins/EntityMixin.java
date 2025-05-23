package me.lily.bllry.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.ChangePitchEvent;
import me.lily.bllry.events.impl.ChangeYawEvent;
import me.lily.bllry.events.impl.UpdateVelocityEvent;
import me.lily.bllry.modules.impl.movement.NoSlowModule;
import me.lily.bllry.modules.impl.movement.VelocityModule;
import me.lily.bllry.modules.impl.player.RotationLockModule;
import me.lily.bllry.utils.IMinecraft;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements IMinecraft {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void pushAwayFrom(Entity entity, CallbackInfo info) {
        if ((Object) this == mc.player && Bllry.MODULE_MANAGER.getModule(VelocityModule.class).isToggled() && Bllry.MODULE_MANAGER.getModule(VelocityModule.class).antiPush.getValue()) {
            info.cancel();
        }
    }

    @ModifyExpressionValue(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d updateMovementInFluid(Vec3d vec3d) {
        if ((Object) this == mc.player && Bllry.MODULE_MANAGER.getModule(VelocityModule.class).isToggled() && Bllry.MODULE_MANAGER.getModule(VelocityModule.class).antiLiquidPush.getValue()) {
            return new Vec3d(0, 0, 0);
        }

        return vec3d;
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    private void updateVelocity(float speed, Vec3d movementInput, CallbackInfo info) {
        if ((Object) this != mc.player) return;

        UpdateVelocityEvent event = new UpdateVelocityEvent(movementInput, speed);
        Bllry.EVENT_HANDLER.post(event);
        if (event.isCancelled()) {
            info.cancel();
            mc.player.setVelocity(mc.player.getVelocity().add(event.getVelocity()));
        }
    }

    @Inject(method = "getYaw*", at = @At("HEAD"), cancellable = true)
    private void getYaw(CallbackInfoReturnable<Float> info) {
        if (Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).isToggled() && (Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).mode.getValue().equals("Yaw") || Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).mode.getValue().equals("Both")) && (Object) this == mc.player) {
            info.setReturnValue(Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).yaw.getValue().floatValue());
        }
    }

    @Inject(method = "getPitch*", at = @At("HEAD"), cancellable = true)
    private void getPitch(CallbackInfoReturnable<Float> info) {
        if (Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).isToggled() && (Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).mode.getValue().equals("Pitch") || Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).mode.getValue().equals("Both")) && (Object) this == mc.player) {
            info.setReturnValue(Bllry.MODULE_MANAGER.getModule(RotationLockModule.class).pitch.getValue().floatValue());
        }
    }

    @Inject(method = "setYaw", at = @At("HEAD"), cancellable = true)
    private void setYaw(float yaw, CallbackInfo info) {
        if ((Object) this != mc.player) return;
        Bllry.EVENT_HANDLER.post(new ChangeYawEvent(yaw));
    }

    @Inject(method = "setPitch", at = @At("HEAD"), cancellable = true)
    private void setPitch(float pitch, CallbackInfo info) {
        if ((Object) this != mc.player) return;
        Bllry.EVENT_HANDLER.post(new ChangePitchEvent(pitch));
    }

    @ModifyExpressionValue(method = "getVelocityMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
    private Block getVelocityMultiplier(Block original) {
        if (Bllry.MODULE_MANAGER.getModule(NoSlowModule.class).isToggled()) {
            if ((original == Blocks.SOUL_SAND && Bllry.MODULE_MANAGER.getModule(NoSlowModule.class).soulSand.getValue()) || (original == Blocks.HONEY_BLOCK && Bllry.MODULE_MANAGER.getModule(NoSlowModule.class).honeyBlocks.getValue())) {
                return Blocks.STONE;
            }
        }

        return original;
    }
}

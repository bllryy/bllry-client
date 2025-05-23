package me.lily.bllry.mixins;

import me.lily.bllry.Bllry;
import me.lily.bllry.modules.impl.visuals.NoRenderModule;
import me.lily.bllry.utils.IMinecraft;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin implements IMinecraft {
    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private static <T extends BlockEntity> void render(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && !Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("None")) {
            if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("Always") || (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("Distance") && Math.sqrt(mc.player.squaredDistanceTo(blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ())) > Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileDistance.getValue().floatValue())) {
                info.cancel();
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && !Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("None")) {
            if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("Always") || (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileEntities.getValue().equals("Distance") && Math.sqrt(mc.player.squaredDistanceTo(blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ())) > Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).tileDistance.getValue().floatValue())) {
                info.cancel();
            }
        }
    }
}

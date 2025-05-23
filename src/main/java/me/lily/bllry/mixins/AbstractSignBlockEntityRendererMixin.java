package me.lily.bllry.mixins;

import me.lily.bllry.Bllry;
import me.lily.bllry.modules.impl.visuals.NoRenderModule;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignBlockEntityRenderer.class)
public class AbstractSignBlockEntityRendererMixin {
    @Inject(method = "renderText", at = @At("HEAD"), cancellable = true)
    private void renderText(BlockPos pos, SignText signText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int lineHeight, int lineWidth, boolean front, CallbackInfo info) {
        if (Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Bllry.MODULE_MANAGER.getModule(NoRenderModule.class).signText.getValue()) {
            info.cancel();
        }
    }
}

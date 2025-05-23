package me.lily.bllry.mixins;

import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.KeyInputEvent;
import me.lily.bllry.events.impl.UnfilteredKeyInputEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        Bllry.EVENT_HANDLER.post(new UnfilteredKeyInputEvent(key, scancode, action, modifiers));
        if (window == client.getWindow().getHandle() && action == 1 && client.currentScreen == null) {
            Bllry.EVENT_HANDLER.post(new KeyInputEvent(key, modifiers));
        }
    }
}

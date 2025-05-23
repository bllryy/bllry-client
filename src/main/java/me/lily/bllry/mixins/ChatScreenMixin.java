package me.lily.bllry.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.ChatInputEvent;
import me.lily.bllry.events.impl.CommandInputEvent;
import me.lily.bllry.modules.impl.core.HUDModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V"), cancellable = true)
    private void sendMessage(String chatText, boolean addToHistory, CallbackInfo info) {
        ChatInputEvent event = new ChatInputEvent(chatText);
        Bllry.EVENT_HANDLER.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatCommand(Ljava/lang/String;)V"), cancellable = true)
    private void sendCommand(String chatText, boolean addToHistory, CallbackInfo info) {
        CommandInputEvent event = new CommandInputEvent(chatText);
        Bllry.EVENT_HANDLER.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private boolean render(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
        return !Bllry.MODULE_MANAGER.getModule(HUDModule.class).isToggled();
    }
}

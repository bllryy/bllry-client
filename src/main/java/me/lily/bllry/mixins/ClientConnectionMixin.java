package me.lily.bllry.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.ClientDisconnectEvent;
import me.lily.bllry.events.impl.PacketReceiveEvent;
import me.lily.bllry.events.impl.PacketSendEvent;
import me.lily.bllry.modules.impl.miscellaneous.AntiPacketKickModule;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void send$HEAD(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo info) {
        PacketSendEvent event = new PacketSendEvent(packet);
        Bllry.EVENT_HANDLER.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("TAIL"), cancellable = true)
    private void send$TAIL(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo info) {
        Bllry.EVENT_HANDLER.post(new PacketSendEvent.Post(packet));
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo info) {
        PacketReceiveEvent event = new PacketReceiveEvent(packet);
        Bllry.EVENT_HANDLER.post(event);

        if (packet instanceof BundleS2CPacket bundleS2CPacket) {
            for (Packet<?> subPacket : bundleS2CPacket.getPackets()) {
                Bllry.EVENT_HANDLER.post(new PacketReceiveEvent(subPacket));
            }
        }

        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V", at = @At("HEAD"))
    private void disconnect(DisconnectionInfo disconnectionInfo, CallbackInfo info) {
        Bllry.EVENT_HANDLER.post(new ClientDisconnectEvent());
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void exceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo info) {
        if (Bllry.MODULE_MANAGER.getModule(AntiPacketKickModule.class).isToggled()) {
            Bllry.CHAT_MANAGER.error("An exception happened in the packet handler. Check stacktrace for more details.");
            Bllry.LOGGER.error("The packet handler has thrown an exception!", ex);
            info.cancel();
        }
    }
}

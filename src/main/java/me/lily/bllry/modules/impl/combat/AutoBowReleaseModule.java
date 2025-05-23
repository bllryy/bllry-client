package me.lily.bllry.modules.impl.combat;

import me.lily.bllry.Bllry;
import me.lily.bllry.events.SubscribeEvent;
import me.lily.bllry.events.impl.PlayerUpdateEvent;
import me.lily.bllry.modules.Module;
import me.lily.bllry.modules.RegisterModule;
import me.lily.bllry.settings.impl.NumberSetting;
import me.lily.bllry.utils.minecraft.NetworkUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

@RegisterModule(name = "AutoBowRelease", description = "Automatically releases your bow after a certain amount of time has passed.", category = Module.Category.COMBAT)
public class AutoBowReleaseModule extends Module {
    public NumberSetting ticks = new NumberSetting("Ticks", "The number of ticks that have to be waited for before releasing the bow.", 3, 0, 20);

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (Bllry.MODULE_MANAGER.getModule(SelfBowModule.class).isToggled()) return;
        if ((mc.player.getOffHandStack().getItem() == Items.BOW || mc.player.getMainHandStack().getItem() == Items.BOW) && mc.player.isUsingItem()) {
            if (mc.player.getItemUseTime() >= ticks.getValue().intValue()) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                NetworkUtils.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(mc.player.getOffHandStack().getItem() == Items.BOW ? Hand.OFF_HAND : Hand.MAIN_HAND, id, mc.player.getYaw(), mc.player.getPitch()));
                mc.player.stopUsingItem();
            }
        }
    }

    @Override
    public String getMetaData() {
        return String.valueOf(ticks.getValue().intValue());
    }
}

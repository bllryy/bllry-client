package me.lily.bllry.modules.impl.player;

import me.lily.bllry.Bllry;
import me.lily.bllry.modules.Module;
import me.lily.bllry.modules.RegisterModule;
import me.lily.bllry.settings.impl.ModeSetting;
import me.lily.bllry.utils.minecraft.InventoryUtils;
import me.lily.bllry.utils.minecraft.NetworkUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@RegisterModule(name = "ThrowFirework", description = "Automatically switches to fireworks and throws them.", category = Module.Category.PLAYER)
public class ThrowFireworkModule extends Module {
    public ModeSetting autoSwitch = new ModeSetting("Switch", "The mode that will be used for automatically switching to necessary items.", "Silent", InventoryUtils.SWITCH_MODES);

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            setToggled(false);
            return;
        }

        if (autoSwitch.getValue().equalsIgnoreCase("None") && mc.player.getMainHandStack().getItem() != Items.FIREWORK_ROCKET) {
            Bllry.CHAT_MANAGER.tagged("You are currently not holding any fireworks.", getName());
            setToggled(false);
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.FIREWORK_ROCKET))) {
            setToggled(false);
            return;
        }

        int slot = InventoryUtils.find(Items.FIREWORK_ROCKET, 0, autoSwitch.getValue().equalsIgnoreCase("AltSwap") || autoSwitch.getValue().equalsIgnoreCase("AltPickup") ? 35 : 8);
        int previousSlot = mc.player.getInventory().selectedSlot;

        if (slot == -1) {
            Bllry.CHAT_MANAGER.tagged("No fireworks could be found in your hotbar.", getName());
            setToggled(false);
            return;
        }

        InventoryUtils.switchSlot(autoSwitch.getValue(), slot, previousSlot);
        NetworkUtils.sendSequencedPacket(sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch()));
        InventoryUtils.switchBack(autoSwitch.getValue(), slot, previousSlot);

        setToggled(false);
    }
}

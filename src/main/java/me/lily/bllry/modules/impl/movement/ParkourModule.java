package me.lily.bllry.modules.impl.movement;

import me.lily.bllry.events.SubscribeEvent;
import me.lily.bllry.events.impl.TickEvent;
import me.lily.bllry.modules.Module;
import me.lily.bllry.modules.RegisterModule;

@RegisterModule(name = "Parkour", description = "Automatically jumps whenever you're at the edge of a block.", category = Module.Category.MOVEMENT)
public class ParkourModule extends Module {
    private boolean jumping = false;

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isOnGround() && !mc.player.isSneaking() && mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001))) {
            mc.options.jumpKey.setPressed(true);
            jumping = true;
        } else if (jumping) {
            jumping = false;
            mc.options.jumpKey.setPressed(false);
        }
    }

    @Override
    public void onDisable() {
        if (jumping) {
            mc.options.jumpKey.setPressed(false);
            jumping = false;
        }
    }
}

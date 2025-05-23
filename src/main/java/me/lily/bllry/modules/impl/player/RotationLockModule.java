package me.lily.bllry.modules.impl.player;

import me.lily.bllry.modules.Module;
import me.lily.bllry.modules.RegisterModule;
import me.lily.bllry.settings.impl.ModeSetting;
import me.lily.bllry.settings.impl.NumberSetting;

@RegisterModule(name = "RotationLock", description = "Locks your rotation to a certain yaw and pitch.", category = Module.Category.PLAYER)
public class RotationLockModule extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Whether the pitch or yaw will be locked.", "Both", new String[]{"Yaw", "Pitch", "Both"});
    public NumberSetting yaw = new NumberSetting("Yaw", "The degrees at which your yaw will be locked.", new ModeSetting.Visibility(mode, "Yaw", "Both"), 0.0f, -180.0f, 180.0f);
    public NumberSetting pitch = new NumberSetting("Pitch", "The degrees at which your pitch will be locked.", new ModeSetting.Visibility(mode, "Pitch", "Both"), 0.0f, -90.0f, 90.0f);

    @Override
    public String getMetaData() {
        return yaw.getValue().floatValue() + ", " + pitch.getValue().floatValue();
    }
}

package me.lily.bllry.modules;

import lombok.Getter;
import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.ToggleModuleEvent;
import me.lily.bllry.settings.Setting;
import me.lily.bllry.settings.impl.*;
import me.lily.bllry.utils.IMinecraft;
import me.lily.bllry.utils.animations.Animation;
import me.lily.bllry.utils.animations.Easing;
import me.lily.bllry.utils.chat.ChatUtils;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Module implements IMinecraft {
    private final String name, description;
    private final Category category;

    private final boolean persistent;
    private boolean toggled;
    private final List<Setting> settings;

    public BooleanSetting chatNotify;
    public BooleanSetting drawn;
    public BindSetting bind;

    private final Animation animationOffset;

    public Module() {
        RegisterModule annotation = getClass().getAnnotation(RegisterModule.class);

        name = annotation.name();
        description = annotation.description();
        category = annotation.category();
        persistent = annotation.persistent();
        toggled = annotation.toggled();
        settings = new ArrayList<>();
        animationOffset = new Animation(300, Easing.Method.EASE_OUT_CUBIC);

        chatNotify = new BooleanSetting("ChatNotify", "Notifies you in chat whenever the module gets toggled on or off.", true);
        drawn = new BooleanSetting("Drawn", "Renders the module's name on the HUD's module list.", annotation.drawn());
        bind = new BindSetting("Bind", "The keybind that toggles the module on and off.", annotation.bind());

        if (persistent) toggled = true;
        if (toggled) {
            Bllry.EVENT_HANDLER.subscribe(this);
        }
    }

    public boolean getNull() {
        return (mc.player == null || mc.world == null);
    }

    public void onEnable() {}
    public void onDisable() {}

    public String getMetaData() {
        return "";
    }

    public void setToggled(boolean toggled) {
        setToggled(toggled, true);
    }

    public void setToggled(boolean toggled, boolean notify) {
        if (persistent) return;
        if (toggled == this.toggled) return;

        this.toggled = toggled;
        Bllry.EVENT_HANDLER.post(new ToggleModuleEvent(this, this.toggled));

        if (this.toggled) {
            animationOffset.setEasing(Easing.Method.EASE_OUT_CUBIC);

            if (notify && chatNotify.getValue()) {
                Bllry.CHAT_MANAGER.message(ChatUtils.getPrimary() + name + ChatUtils.getSecondary() + ".toggled = " + Formatting.GREEN + "true" + ChatUtils.getSecondary() + ";", "toggle-" + getName().toLowerCase());
            }

            onEnable();
            if (this.toggled) Bllry.EVENT_HANDLER.subscribe(this);
        } else {
            animationOffset.setEasing(Easing.Method.EASE_IN_CUBIC);

            Bllry.EVENT_HANDLER.unsubscribe(this);
            onDisable();

            if (notify && chatNotify.getValue()) {
                Bllry.CHAT_MANAGER.message(ChatUtils.getPrimary() + name + ChatUtils.getSecondary() + ".toggled = " + Formatting.RED + "false" + ChatUtils.getSecondary() + ";", "toggle-" + getName().toLowerCase());
            }
        }
    }

    public int getBind() {
        return bind.getValue();
    }

    public void setBind(int bind) {
        this.bind.setValue(bind);
    }

    public void resetValues() {
        for (Setting uncastedSetting : settings) {
            if (uncastedSetting instanceof BooleanSetting setting) setting.resetValue();
            if (uncastedSetting instanceof NumberSetting setting) setting.resetValue();
            if (uncastedSetting instanceof ModeSetting setting) setting.resetValue();
            if (uncastedSetting instanceof StringSetting setting) setting.resetValue();
            if (uncastedSetting instanceof BindSetting setting) setting.resetValue();
            if (uncastedSetting instanceof WhitelistSetting setting) setting.clear();
            if (uncastedSetting instanceof ColorSetting setting) setting.resetValue();
        }
    }

    public Setting getSetting(String name) {
        return settings.stream().filter(s -> s.getName().equalsIgnoreCase(name) && !(s instanceof CategorySetting)).findFirst().orElse(null);
    }

    @Getter
    public enum Category {
        COMBAT("Combat"),
        PLAYER("Player"),
        VISUALS("Visuals"),
        MOVEMENT("Movement"),
        MISCELLANEOUS("Miscellaneous"),
        CORE("Core");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }
}

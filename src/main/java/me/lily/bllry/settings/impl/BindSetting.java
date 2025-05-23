package me.lily.bllry.settings.impl;

import lombok.Getter;
import lombok.Setter;
import me.lily.bllry.Bllry;
import me.lily.bllry.events.impl.SettingChangeEvent;
import me.lily.bllry.settings.Setting;

@Getter @Setter
public class BindSetting extends Setting {
    private int value;
    private final int defaultValue;

    public BindSetting(String name, String description, int value) {
        super(name, name, description, new Setting.Visibility());
        this.value = value;
        this.defaultValue = value;
    }

    public BindSetting(String name, String tag, String description, int value) {
        super(name, tag, description, new Setting.Visibility());
        this.value = value;
        this.defaultValue = value;
    }

    public BindSetting(String name, String description, Setting.Visibility visibility, int value) {
        super(name, name, description, visibility);
        this.value = value;
        this.defaultValue = value;
    }

    public BindSetting(String name, String tag, String description, Setting.Visibility visibility, int value) {
        super(name, tag, description, visibility);
        this.value = value;
        this.defaultValue = value;
    }

    public void resetValue() {
        value = defaultValue;
    }

    public void setValue(int value) {
        this.value = value;
        Bllry.EVENT_HANDLER.post(new SettingChangeEvent(this));
    }

    public static class Visibility extends Setting.Visibility {
        private final BindSetting value;
        private final int targetValue;

        public Visibility(BindSetting value, int targetValue) {
            super(value);
            this.value = value;
            this.targetValue = targetValue;
        }

        @Override
        public void update() {
            if (value.getVisibility() != null) {
                value.getVisibility().update();
                if (!value.getVisibility().isVisible()) {
                    setVisible(false);
                    return;
                }
            }

            setVisible(value.getValue() == targetValue);
        }
    }
}

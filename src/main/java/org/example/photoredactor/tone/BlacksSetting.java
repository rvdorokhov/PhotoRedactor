package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class BlacksSetting extends SettingsWithSameCoefs {
    public BlacksSetting() {
        changeRGB = (color, coef) -> color * (Math.exp(1 - coef));;
    }

}

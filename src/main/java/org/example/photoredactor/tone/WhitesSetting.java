package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class WhitesSetting extends SettingsWithSameCoefs {
    public WhitesSetting() {
        changeRGB = (color, coef) -> color * (Math.exp(coef - 1));;
    }
}

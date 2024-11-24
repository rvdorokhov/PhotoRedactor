package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingWithSameCoefs;

public class ExposeSetting extends SettingWithSameCoefs {
    public ExposeSetting() {
        changeRGB = (color, coef) -> Math.pow(color/255.0, (2 - coef)) * 255;
    }
}
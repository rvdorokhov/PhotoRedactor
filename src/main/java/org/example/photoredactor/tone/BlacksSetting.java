package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class BlacksSetting extends SettingsWithSameCoefs {
    public BlacksSetting() {
        final double a = 0.7;
        final double b = 1.5;
        changeRGB = (color, coef) ->
                color + (1 - color / 255.0) * (-color * Math.exp(-a * coef) + b) * (1 - coef);
    }
}

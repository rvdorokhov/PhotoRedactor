package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class WhitesSetting extends SettingsWithSameCoefs {
    public WhitesSetting() {
        // Рассчитывается как color * coef
        // Коэффициент высчитывается по формуле
        // y = { e^(x - 1),         e^(x - 1) >= 1
        //     { e^(a * x - 1) + b, e^(x - 1) < 1
        final double a = 0.3;
        final double b = 0.5;
        changeRGB = (color, coef) ->
                (Math.exp(coef - 1) >= 1) ? color * (Math.exp(coef - 1))
                        : color * (Math.exp(a * coef - 1) + b);
    }
}

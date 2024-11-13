package org.example.photoredactor.WB;

import org.example.photoredactor.settings.Settings;

public class TintSetting extends Settings {
    protected final double CANAL_RED_COEF = 0.2;
    protected final double CANAL_BLUE_COEF = 0.2;

    public TintSetting() {
        changeR = (color, coef) ->
                color * (1 + CANAL_RED_COEF * (1 - coef));
        changeG = (color, coef) ->
                color;
        changeB = (color, coef) ->
                color * (1 - CANAL_BLUE_COEF * (1 - coef));
    }
}

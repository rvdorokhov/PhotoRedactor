package org.example.photoredactor.WB;

import org.example.photoredactor.settings.Settings;

public class TempSetting extends Settings {
    protected final double CANAL_RED_COEF = -0.7;
    protected final double CANAL_GREEN_COEF = -1.0;
    protected final double CANAL_BLUE_COEF = -0.0;

    public TempSetting() {
        changeR = (color, coef) ->
                color * (1 + CANAL_RED_COEF * (1 - coef));
        changeG = (color, coef) ->
                color * (1 + CANAL_GREEN_COEF * (1 - coef));
        changeB = (color, coef) ->
                color * (1 + CANAL_BLUE_COEF * (1 - coef));
    }
}

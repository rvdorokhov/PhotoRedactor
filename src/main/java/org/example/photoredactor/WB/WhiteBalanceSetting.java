package org.example.photoredactor.WB;

import org.example.photoredactor.settings.SettingsPixelEdit;

public class WhiteBalanceSetting extends SettingsPixelEdit {
    protected final double CANAL_RED_COEF;
    protected final double CANAL_GREEN_COEF;
    protected final double CANAL_BLUE_COEF;

    public WhiteBalanceSetting(double redCoef, double greenCoef, double blueCoef) {
        this.CANAL_RED_COEF = redCoef;
        this.CANAL_GREEN_COEF = greenCoef;
        this.CANAL_BLUE_COEF = blueCoef;

        changeR = (color, coef) ->
                color * (1 + CANAL_RED_COEF * (1 - coef));
        changeG = (color, coef) ->
                color * (1 + CANAL_GREEN_COEF * (1 - coef));
        changeB = (color, coef) ->
                color * (1 + CANAL_BLUE_COEF * (1 - coef));
    }
}

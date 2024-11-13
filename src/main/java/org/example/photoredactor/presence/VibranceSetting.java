package org.example.photoredactor.presence;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class VibranceSetting extends SettingsWithSameCoefs {
    protected double greyComponent;
    protected double curVibrance; // Характеристика-текущая красочность пиксели
                                  // Используется для более равномерного изменения красочности пикселей
    protected final double COEF_R = 0.2126;
    protected final double COEF_G = 0.7152;
    protected final double COEF_B = 0.0722;

    @Override
    public double[] change(double[] rgb) {
        greyComponent = COEF_R * rgb[INDEX_R] + COEF_G * rgb[INDEX_G] + COEF_B * rgb[INDEX_B];
        curVibrance = (Math.abs(rgb[INDEX_R] - greyComponent) + Math.abs(rgb[INDEX_G] - greyComponent) + Math.abs(rgb[INDEX_B] - greyComponent)) / (3.0 * 255);

        changeRGB = (color, coef) ->
                greyComponent + (color - greyComponent) * (1 + (coef - 1) * (1 - curVibrance));

        return super.change(rgb);
    }
}

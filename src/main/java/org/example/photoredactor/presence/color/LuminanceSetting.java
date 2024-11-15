package org.example.photoredactor.presence;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public abstract class LuminanceSetting extends SettingsWithSameCoefs {
    protected double greyComponent;

    protected final double COEF_R = 0.2126;
    protected final double COEF_G = 0.7152;
    protected final double COEF_B = 0.0722;

    @Override
    public double[] change(double[] rgb) {
        greyComponent = COEF_R * rgb[INDEX_R] + COEF_G * rgb[INDEX_G] + COEF_B * rgb[INDEX_B];

        return super.change(rgb);
    }
}

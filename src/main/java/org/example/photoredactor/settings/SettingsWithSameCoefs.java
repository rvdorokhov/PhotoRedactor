package org.example.photoredactor.settings;

import java.util.function.BiFunction;

public abstract class SettingsWithSameCoefs extends SettingsPixelEdit {
    protected BiFunction<Double, Double, Double> changeRGB;

    public double[] changePixel(double[] rgb) {
        changeB = changeRGB;
        changeR = changeRGB;
        changeG = changeRGB;

        return super.changePixel(rgb);
    }
}

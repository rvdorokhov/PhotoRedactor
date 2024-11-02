package org.example.photoredactor.settings;

import java.util.function.BiFunction;

public abstract class SettingsWithSameCoefs extends Settings{
    protected BiFunction<Double, Double, Double> changeRGB;

    public double[] change(double[] rgb) {
        changeB = changeRGB;
        changeR = changeRGB;
        changeG = changeRGB;

        return super.change(rgb);
    }
}

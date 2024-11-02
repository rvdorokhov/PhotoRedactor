package org.example.photoredactor.tone;

public class LightsSetting extends ExposeSetting {
    // Начиная с какого значения на пиксели будет накладываться эффект
    protected static double BORDER = 100;

    public double[] change(double[] rgb) {
        double averageLightness = getAverage(rgb);

        if (averageLightness >= BORDER) {
            return super.change(rgb);
        } else {
            return rgb;
        }
    }
}

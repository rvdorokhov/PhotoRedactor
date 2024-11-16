package org.example.photoredactor.tone;

public class LightsSetting extends ExposeSetting {
    // Начиная с какого значения на пиксели будет накладываться эффект
    protected static double BORDER = 100;

    public double[] changePixel(double[] rgb) {
        double averageLightness = getAverage(rgb);

        if (averageLightness >= BORDER) {
            return super.changePixel(rgb);
        } else {
            return rgb;
        }
    }
}

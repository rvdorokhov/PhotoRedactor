package org.example.photoredactor.tone;

public class ShadowsSetting extends ExposeSetting {
    // Начиная с какого значения на пиксели будет накладываться эффект
    // Ахтунг - значение не должно быть больше аналогичного у LightsSetting
    // Иначе будет некорректная обработка
    protected static double BORDER = 100;

    public double[] change(double[] rgb) {
        double averageLightness = getAverage(rgb);

        if (averageLightness < BORDER) {
            return super.change(rgb);
        } else {
            return rgb;
        }
    }
}

package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingWithSameCoefs;

public class ContrSetting extends SettingWithSameCoefs {
    // Отвечает за плавность перехода. Чем меньше значение, тем плавнее переход
    // Объяснить сложно, для понимания лучше поиграться с коэффициентом
    protected double SOFT_COEF = 0.25;

    public double[] changePixel(double[] rgb) {
        if (coef != 1) {
            changeRGB = (color, coef) ->
                    color + SOFT_COEF * (128 + coef * (color - 128) - color);
        } else {
            changeRGB = (color, coef) ->
                    color;
        }

        return super.changePixel(rgb);
    }
}

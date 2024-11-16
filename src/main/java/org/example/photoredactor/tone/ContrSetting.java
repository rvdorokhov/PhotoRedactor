package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class ContrSetting extends SettingsWithSameCoefs {
    // Отвечает за плавность перехода. Чем меньше значение, тем плавнее переход
    // Объяснить сложно, для понимания лучше поиграться с коэффициентом
    protected double SOFT_COEF = 0.25;

    public void setCoef(double coef) {
        // добавляем в конец, потому что специфика функции изменения пикселя
        // не позволяет ее использовать как коэффициент
        dequeAddLast(this);
        super.setCoef(coef);
    }

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

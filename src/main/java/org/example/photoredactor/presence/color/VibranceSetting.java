package org.example.photoredactor.presence;

public class VibranceSetting extends LuminanceSetting {
    protected double curVibrance; // Характеристика-текущая красочность пиксели
                                  // Используется для более равномерного изменения красочности пикселей

    @Override
    public double[] change(double[] rgb) {
        curVibrance = (Math.abs(rgb[INDEX_R] - greyComponent) + Math.abs(rgb[INDEX_G] - greyComponent) + Math.abs(rgb[INDEX_B] - greyComponent)) / (3.0 * 255);

        changeRGB = (color, coef) ->
                greyComponent + (color - greyComponent) * (1 + (coef - 1) * (1 - curVibrance));

        return super.change(rgb);
    }
}

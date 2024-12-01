package org.example.photoredactor.presence.detail;

import org.example.photoredactor.settings.Setting;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public abstract class DetailSetting extends Setting {
    protected double side;
    protected double center;
    protected Mat kernel = new Mat(3, 3, CvType.CV_32F);
    {
        coef = 0;
    }

    // Превращает введенное число, находящееся в диапазоне от 0 до 200, в коэффициент от 0 до 2
    protected double convertToCoefficient(double value) {
        return value / 100;
    }

    public void resetCoef() {
        coef = 0;
    }
}
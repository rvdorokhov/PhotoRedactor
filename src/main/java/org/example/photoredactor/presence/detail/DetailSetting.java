package org.example.photoredactor.presence.detail;

import org.example.photoredactor.settings.SettingsImageEdit;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public abstract class DetailSetting extends SettingsImageEdit {
    protected double side;
    protected double center;
    protected Mat kernel;

    public void changeMatImage(Mat image, double coef) {
        setCoef(coef);

        kernel = new Mat(3, 3, CvType.CV_32F);
    }

    // Превращает введенное число, находящееся в диапазоне от 0 до 200, в коэффициент от 0 до 2
    protected double convertToCoefficient(double value) {
        return value / 100;
    }
}

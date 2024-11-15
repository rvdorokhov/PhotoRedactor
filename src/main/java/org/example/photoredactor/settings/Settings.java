package org.example.photoredactor.settings;

import org.opencv.core.Mat;

public abstract class Settings {
    protected double coef;

    // Превращает введенное число, находящееся в диапазоне от -100 до 100, в коэффициент от 1 до -1
    protected double convertToCoefficient(double value) {
        return 1 + value / 100;
    }

    // Выбирает значение между 0, value и 255
    protected double chooseCorrectValue(double value) {
        double result;
        result = Math.min(255, value);
        result = Math.max(0, result);
        return result;
    }

    protected void setCoef(double coef) {
        this.coef = convertToCoefficient(coef);
    }

   // реализация для абстрактной rgb-матрицы, без привязки к классу Mat библиотеки OpenCV
   // public abstract void changeImage(double[][][] rgbMatrix, double coef);

    public abstract void changeMatImage(Mat image, double coef);
}

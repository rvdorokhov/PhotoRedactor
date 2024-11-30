package org.example.photoredactor.settings;

import org.opencv.core.Mat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;

public abstract class Setting {
    // дек вызовов - определяет, в каком порядке будут применяться внесенные настройки
    protected static Deque<Setting> dequeCalls = new ArrayDeque<>();

    protected double coef = 1;

    // Превращает введенное число, находящееся в диапазоне от -100 до 100, в коэффициент от 0 до 2
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

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = convertToCoefficient(coef);
    }

    public void setCoefWithoutConvertation(double coef) {
        this.coef = coef;
    }

    public void resetCoef() {
        coef = 1;
    }

   // реализация для абстрактной rgb-матрицы, без привязки к классу Mat библиотеки OpenCV
   // public abstract void changeImage(double[][][] rgbMatrix, double coef);

    public static void changeImage(Mat image) {
        for (Setting setting : dequeCalls) {
            setting.applySetting(image);
        }
    }

    public void changeImage(Mat image, double coef) {
        setCoef(coef);
        changeImage(image);
    }

    public static void initSettings(Setting... settings) {
        dequeCalls.clear();
        dequeCalls.addAll(Arrays.asList(settings));
    }

    public static void initSettings(Collection<Setting> settings) {
        dequeCalls.clear();
        dequeCalls.addAll(settings);
    }

    public static void resetSettings() {
        for (Setting setting : dequeCalls) {
            setting.resetCoef();
        }
    }

    protected abstract void applySetting(Mat image);
}

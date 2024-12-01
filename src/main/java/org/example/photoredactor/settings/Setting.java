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

    protected abstract void applySetting(Mat image);
}

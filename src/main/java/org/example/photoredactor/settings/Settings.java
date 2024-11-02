package org.example.photoredactor.settings;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;

public abstract class Settings {
    protected double coef;
    // дек вызовов - определяет, в каком порядке будут применяться внесенные настройки
    protected static Deque<Settings> dequeCalls = new ArrayDeque<>();

    protected BiFunction<Double, Double, Double> changeB;
    protected BiFunction<Double, Double, Double> changeR;
    protected BiFunction<Double, Double, Double> changeG;

    protected static int INDEX_B = 0;
    protected static int INDEX_R = 1;
    protected static int INDEX_G = 2;

    // Превращает введенное число, находящееся в диапазоне от -100 до 100, в коэффициент от 1 до -1
    protected double convertToCoefficient(double value) {
        return 1 + value / 100;
    }

    // Выбирает значение между 0, value и 255
    protected double chooseCorrectValue(double value) {
        double result;
        result = Math.min(255, value);
        result = Math.max(0, value);
        return result;
    }

    public void setCoef(double coef) {
        dequeAddFirst(this);
        this.coef = convertToCoefficient(coef);
    }

    public double[] change(double[] rgb) {
        double[] newRGB = rgb.clone();
        for (Settings setting : dequeCalls) {
            newRGB[INDEX_B] = setting.changeB(newRGB[INDEX_B]);
            newRGB[INDEX_R] = setting.changeR(newRGB[INDEX_R]);
            newRGB[INDEX_G] = setting.changeG(newRGB[INDEX_G]);
        }

        return newRGB;
    }

    protected static void dequeAddFirst(Settings setting) {
        if (!dequeCalls.contains(setting)) {
            dequeCalls.addFirst(setting);
        }
    }

    protected static void dequeAddLast(Settings setting) {
        if (!dequeCalls.contains(setting)) {
            dequeCalls.addLast(setting);
        }
    }

    protected double changeR(double r) {
        return chooseCorrectValue(changeR.apply(r, coef));
    }

    protected double changeB(double b) {
        return chooseCorrectValue(changeB.apply(b, coef));
    }

    protected double changeG(double g) {
        return chooseCorrectValue(changeG.apply(g, coef));
    }

    protected double getAverage(double[] rgb) {
        return (rgb[INDEX_B] + rgb[INDEX_R] + rgb[INDEX_G]) / 3;
    }
}
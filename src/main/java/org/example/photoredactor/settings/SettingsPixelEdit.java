package org.example.photoredactor.settings;


import org.opencv.core.Mat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;

public abstract class SettingsPixelEdit extends Settings{
    // дек вызовов - определяет, в каком порядке будут применяться внесенные настройки
    protected static Deque<SettingsPixelEdit> dequeCalls = new ArrayDeque<>();

    protected BiFunction<Double, Double, Double> changeB;
    protected BiFunction<Double, Double, Double> changeR;
    protected BiFunction<Double, Double, Double> changeG;

    protected static int INDEX_B = 0;
    protected static int INDEX_R = 1;
    protected static int INDEX_G = 2;

    protected double[] changePixel(double[] rgb) {
        double[] newRGB = rgb.clone();
        for (SettingsPixelEdit setting : dequeCalls) {
            newRGB[INDEX_B] = setting.changeB(newRGB[INDEX_B]);
            newRGB[INDEX_R] = setting.changeR(newRGB[INDEX_R]);
            newRGB[INDEX_G] = setting.changeG(newRGB[INDEX_G]);
        }

        return newRGB;
    }

    public void changeImage(double[][][] rgbMatrix, double coef) {
        int rows = rgbMatrix.length;
        int cols = rgbMatrix[0].length;
        setCoef(coef);

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                double[] rgb = changePixel(rgbMatrix[y][x]);
                rgbMatrix[y][x] = rgb.clone();
            }
        }
    }

    public void changeMatImage(Mat image, double coef) {
        int rows = image.rows();
        int cols = image.cols();
        setCoef(coef);

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                double[] rgb = changePixel(image.get(y, x));
                image.put(y, x, rgb);
            }
        }
    }

    protected static void dequeAddFirst(SettingsPixelEdit setting) {
        if (!dequeCalls.contains(setting)) {
            dequeCalls.addFirst(setting);
        }
    }

    protected static void dequeAddLast(SettingsPixelEdit setting) {
        if (!dequeCalls.contains(setting)) {
            dequeCalls.addLast(setting);
        }
    }

    public void setCoef(double coef) {
        dequeAddFirst(this);
        super.setCoef(coef);
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
package org.example.photoredactor.settings;


import org.opencv.core.Mat;

import java.util.function.BiFunction;

public abstract class SettingPixelEdit extends Setting {
    protected BiFunction<Double, Double, Double> changeB;
    protected BiFunction<Double, Double, Double> changeR;
    protected BiFunction<Double, Double, Double> changeG;

    protected static int INDEX_B = 0;
    protected static int INDEX_R = 1;
    protected static int INDEX_G = 2;

/*    public void changeImage(double[][][] rgbMatrix, double coef) {
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
 */
    public void applySetting(Mat image) {
        int rows = image.rows();
        int cols = image.cols();

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                double[] rgb = changePixel(image.get(y, x));
                image.put(y, x, rgb);
            }
        }
    }

    protected double[] changePixel(double[] rgb) {
        double[] newRGB = rgb.clone();

        newRGB[INDEX_B] = changeB(newRGB[INDEX_B]);
        newRGB[INDEX_R] = changeR(newRGB[INDEX_R]);
        newRGB[INDEX_G] = changeG(newRGB[INDEX_G]);

        return newRGB;
    }

    public void setCoef(double coef) {
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
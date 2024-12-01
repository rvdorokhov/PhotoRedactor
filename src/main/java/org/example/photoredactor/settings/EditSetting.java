package org.example.photoredactor.settings;

import org.opencv.core.Mat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;

public class EditSetting {
    // дек вызовов - определяет, в каком порядке будут применяться внесенные настройки
    protected Deque<Setting> dequeCalls = new ArrayDeque<>();

    // реализация для абстрактной rgb-матрицы, без привязки к классу Mat библиотеки OpenCV
    // public abstract void changeImage(double[][][] rgbMatrix, double coef);

    public void changeImage(Mat image) {
        for (Setting setting : dequeCalls) {
            setting.applySetting(image);
        }
    }

    public void initSettings(Setting... settings) {
        dequeCalls.clear();
        dequeCalls.addAll(Arrays.asList(settings));
    }

    public void initSettings(Collection<Setting> settings) {
        dequeCalls.clear();
        dequeCalls.addAll(settings);
    }

    public void resetSettings() {
        for (Setting setting : dequeCalls) {
            setting.resetCoef();
        }
    }
}

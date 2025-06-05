package org.example.photoredactor.settings;

import org.opencv.core.Mat;

import java.util.*;

public class EditSetting {
    // список вызовов - определяет, в каком порядке будут применяться внесенные настройки
    protected List<Setting> calls = new ArrayList<>();

    // реализация для абстрактной rgb-матрицы, без привязки к классу Mat библиотеки OpenCV
    // public abstract void changeImage(double[][][] rgbMatrix, double coef);

    public void changeImage(Mat image) {
        for (Setting setting : calls) {
            setting.applySetting(image);
        }
    }

    public void initSettings(Setting... settings) {
        calls.clear();
        calls.addAll(Arrays.asList(settings));
    }

    public void initSettings(Collection<Setting> settings) {
        calls.clear();
        calls.addAll(settings);
    }

    public void resetSettings() {
        for (Setting setting : calls) {
            setting.resetCoef();
        }
    }
}

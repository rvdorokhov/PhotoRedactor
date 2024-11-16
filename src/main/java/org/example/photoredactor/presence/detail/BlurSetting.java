package org.example.photoredactor.presence.detail;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BlurSetting extends DetailSetting {
    public void changeMatImage(Mat image, double newCoef) {
        super.changeMatImage(image, newCoef);

        // При применении фильтра сверточное ядро влияет на интенсивности пикселей,
        // и если сумма всех элементов ядра не равна 1, изображение может становиться темнее или светлее.
        // Для сохранения исходной яркости сумма элементов ядра должна быть равна 1
        // Это надо учитывать при изменении параметров SIDE и CENTER

        side = coef / 8.0;       // Боковые элементы увеличиваются с ростом newCoef
        center = 1.0 - coef;       // Центральный элемент пропорционален newCoef

        // Нормализация, чтобы сумма ядра равнялась 1
        double normalizationFactor = center + 8 * side;
        center /= normalizationFactor;
        side /= normalizationFactor;

        // Фильтр Кернеля
        // https://en.wikipedia.org/wiki/Kernel_(image_processing)
        kernel.put(0, 0,
                side, side,  side,
                side,  center, side,
                side, side,  side);

        // Применение фильтра
        Imgproc.filter2D(image, image, image.depth(), kernel);
    }
}

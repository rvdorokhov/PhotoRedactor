package org.example.photoredactor.presence.detail;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SharpeningSetting extends DetailSetting {
    public void applySetting(Mat image) {
        // При применении фильтра сверточное ядро влияет на интенсивности пикселей,
        // и если сумма всех элементов ядра не равна 1, изображение может становиться темнее или светлее.
        // Для сохранения исходной яркости сумма элементов ядра должна быть равна 1
        // Это надо учитывать при изменении параметров SIDE и CENTER
        side = -coef;
        center = 1 + coef * 4;

        // Фильтр Кернеля
        // https://en.wikipedia.org/wiki/Kernel_(image_processing)
        kernel.put(0, 0,
                0, side,  0,
                side,  center, side,
                0, side,  0);

        // Применение фильтра
        Imgproc.filter2D(image, image, image.depth(), kernel);
    }
}

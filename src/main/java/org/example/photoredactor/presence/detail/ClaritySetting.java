package org.example.photoredactor.presence.detail;

import org.example.photoredactor.settings.SettingsImageEdit;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ClaritySetting extends SettingsImageEdit {
    public void changeMatImage(Mat image, double newCoef) {
        setCoef(newCoef);
        // Переводим изображение в цветовое пространство LAB
        Mat labImage = new Mat();
        Imgproc.cvtColor(image, labImage, Imgproc.COLOR_LBGR2Lab);

        // Разделяем LAB-каналы
        List<Mat> labChannels = new ArrayList<>(3);
        Core.split(labImage, labChannels);

        // Применяем CLAHE только к L-каналу (яркость)
        CLAHE clahe = Imgproc.createCLAHE(coef, new Size(8, 8));  // clipLimit и tileGridSize
        clahe.apply(labChannels.getFirst(), labChannels.getFirst());

        // Сливаем каналы обратно
        Core.merge(labChannels, labImage);

        // Переводим изображение обратно в BGR
        Imgproc.cvtColor(labImage, image, Imgproc.COLOR_Lab2LBGR);
    }
}

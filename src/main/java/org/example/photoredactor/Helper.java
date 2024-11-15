package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import org.example.photoredactor.settings.Settings;
import org.opencv.core.Mat;

public class Calculator {
    public static void changeImage(Mat image, Settings setting, double coef) {
        int rows = image.rows();
        int cols = image.cols();
        setting.setCoef(coef);

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                double[] rgb = setting.change(image.get(y, x));
                image.put(y, x, rgb);
            }
        }
    }

    public static String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }

    public static double log(double base, double x) {
        return Math.log(x) / Math.log(base);
    }
}

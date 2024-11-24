package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import org.example.photoredactor.settings.Setting;
import org.opencv.core.Mat;

public class Helper {
    public static void changeImage(Mat image, Setting setting, double coef) {
        setting.changeImage(image, coef);
    }

    public static String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }

    public static double log(double base, double x) {
        return Math.log(x) / Math.log(base);
    }
}

package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import org.example.photoredactor.settings.Settings;
import org.example.photoredactor.settings.SettingsPixelEdit;
import org.opencv.core.Mat;

public class Helper {
    public static void changeImage(Mat image, Settings setting, double coef) {
        setting.changeMatImage(image, coef);
    }

    public static String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }

    public static double log(double base, double x) {
        return Math.log(x) / Math.log(base);
    }
}

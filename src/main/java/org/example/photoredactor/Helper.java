package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import org.example.photoredactor.settings.Setting;
import org.opencv.core.Mat;

import java.io.File;

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

    public static String fileToString(File file) {
        return file.toString().replace('\\', '/');
    }

    public static String getImgName(String img) {
        String[] fileNameSplit = img.split("/");
        int ind = fileNameSplit.length - 1;
        return fileNameSplit[ind];
    }
}

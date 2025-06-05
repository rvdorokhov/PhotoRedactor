package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import org.example.photoredactor.settings.Setting;
import org.opencv.core.Mat;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Helper {
    public static Set<String> NOT_USUAL_SETTINGS = new HashSet<>(
            Stream.of("#sharpeningTextField",
                    "#clarityTextField",
                    "#blurTextField").toList());

    public static String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }

    public static String fileToString(File file) {
        return file.toString().replace('\\', '/');
    }

    public static String getImgName(String img) {
        String[] fileNameSplit = img.split("/");
        int ind = fileNameSplit.length - 1;
        return fileNameSplit[ind];
    }

    public static boolean isValueCorrect(double value, Event event) {
        String textFieldId = Helper.getId(event);
        if (NOT_USUAL_SETTINGS.contains(textFieldId)) {
            return (!(value < 0)) && (!(value > 200));
        } else {
            return (!(value < -100)) && (!(value > 100));
        }
    }
}

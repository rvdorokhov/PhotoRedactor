package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import org.example.photoredactor.settings.Setting;
import org.opencv.core.Mat;

import java.io.File;

public class Helper {
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
}

package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainController {
    @FXML private ImageView imageView;

    @FXML
    private void onSliderDrag(Event event) {
        TextSliderConnect.sliderDrag(event, imageView);
    }

    @FXML
    private void onTextFieldEdit(Event event) {
        TextSliderConnect.textFieldEdit(event, imageView);
    }

    @FXML
    private void test(Event event) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = imread("src/main/resources/org/example/photoredactor/IMG_6374.jpg");

        if (!image.empty()) {
            for (int y = 0; y < image.rows(); ++y) {
                for (int x = 0; x < image.cols(); ++x) {
                    double[] rgb = image.get(y, x);
                    for (int p = 0; p <= 2; ++p) {
                        rgb[p] += 50;

                        if (rgb[p] > 255) {
                            rgb[p] = 255;
                        }
                    }
                    image.put(y, x, rgb);
                }
            }

            imwrite("src/main/resources/org/example/photoredactor/IMG_6374_new.jpg", image);

            File file = new File("src/main/resources/org/example/photoredactor/IMG_6374_new.jpg");
            Image img = new Image(file.toURI().toString());
            imageView.setImage(img);
        } else {
            File file = new File("src/main/resources/org/example/photoredactor/IMG_2155.jpg");
            Image img = new Image(file.toURI().toString());
            imageView.setImage(img);
        }
    }
}
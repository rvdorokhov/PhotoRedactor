package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class TextSliderConnect {

    private static int SLIDER_WORDS_COUNT = 6;
    private static int TEXT_FIELD_WORDS_COUNT = 9;

    private static Alert alert = new Alert(Alert.AlertType.ERROR);
    static {
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка ввода");
    }

    private static Pair<Slider, TextField> getObjectsById(ImageView imageView,
                                                              String sliderId,
                                                              String textFieldId) {
        Scene scene  = imageView.getScene();
        Slider slider = (Slider) scene.lookup(sliderId);
        TextField textField = (TextField) scene.lookup(textFieldId);

        return new Pair<>(slider, textField);
    }

    public static void sliderDrag(Event event, ImageView imageView) {
        String sliderId = Helper.getId(event);
        String textFieldId =
                sliderId.substring(0, sliderId.length() - SLIDER_WORDS_COUNT) + "TextField";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getKey();
        TextField textField = objects.getValue();

        String value = String.valueOf(Math.round(slider.getValue()));

        textField.setText(value);
    }

    public static void textFieldEdit(Event event, ImageView imageView) {
        String textFieldId = Helper.getId(event);
        String sliderId =
                textFieldId.substring(0, textFieldId.length() - TEXT_FIELD_WORDS_COUNT) + "Slider";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getKey();
        TextField textField = objects.getValue();

        double value = 0;
        try {
            value = Integer.parseInt(textField.getText());
        } catch (Exception e) {
            alert.setContentText("Введено некорректное значение.");
            value = 0;
            textField.setText("0");
            alert.showAndWait();
        }

        if (!Helper.isValueCorrect(value, event)) {
            if (Helper.NOT_USUAL_SETTINGS.contains(textFieldId)) {
                alert.setContentText("Для параметров Зернистость, Четкость, Размытие " +
                        "введенное значение должно лежать в диапазоне от 0 до 200.");
                alert.showAndWait();
            } else {
                alert.setContentText("Введенное значение должно лежать в диапазоне от -100 до 100.");
                alert.showAndWait();
            }
            value = 0;
            textField.setText("0");
        }

        slider.setValue(value);
    }
}
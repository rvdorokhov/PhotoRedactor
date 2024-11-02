package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class TextSliderConnect {

    private static int SLIDER_WORDS_COUNT = 6;
    private static int TEXT_FIELD_WORDS_COUNT = 9;

    private static Pair<Slider, TextField> getObjectsById(ImageView imageView,
                                                              String sliderId,
                                                              String textFieldId) {
        Scene scene  = imageView.getScene();
        Slider slider = (Slider) scene.lookup(sliderId);
        TextField textField = (TextField) scene.lookup(textFieldId);

        return new Pair<>(slider, textField);
    }

    public static void sliderDrag(Event event, ImageView imageView) {
        String sliderId = Calculator.getId(event);
        String textFieldId =
                sliderId.substring(0, sliderId.length() - SLIDER_WORDS_COUNT) + "TextField";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getKey();
        TextField textField = objects.getValue();

        String value = String.valueOf(Math.round(slider.getValue()));

        textField.setText(value);
    }

    public static void textFieldEdit(Event event, ImageView imageView) {
        String textFieldId = Calculator.getId(event);
        String sliderId =
                textFieldId.substring(0, textFieldId.length() - TEXT_FIELD_WORDS_COUNT) + "Slider";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getKey();
        TextField textField = objects.getValue();

        int value = Integer.parseInt(textField.getText());

        slider.setValue(value);
    }
}
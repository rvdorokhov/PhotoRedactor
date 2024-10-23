package org.example.photoredactor;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import eu.hansolo.toolbox.tuples.Pair;

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
        String sliderId = getId(event);
        String textFieldId =
                sliderId.substring(0, sliderId.length() - SLIDER_WORDS_COUNT) + "TextField";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getA();
        TextField textField = objects.getB();

        String value = String.valueOf(Math.round(slider.getValue()));

        textField.setText(value);
    }

    // Отличается от предыдущего тем, что ползунок не перетаскивается, а просто "кликается"
    public static void sliderEdit(Event event, ImageView imageView) {

    }

    public static void textFieldEdit(Event event, ImageView imageView) {
        String textFieldId = getId(event);
        String sliderId =
                textFieldId.substring(0, textFieldId.length() - TEXT_FIELD_WORDS_COUNT) + "Slider";

        Pair<Slider, TextField> objects = getObjectsById(imageView, sliderId, textFieldId);
        Slider slider = objects.getA();
        TextField textField = objects.getB();

        int value = Integer.parseInt(textField.getText());

        slider.setValue(value);
    }

    public static String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }
}
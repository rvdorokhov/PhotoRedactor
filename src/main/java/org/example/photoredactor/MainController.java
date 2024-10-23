package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class MainController {
    @FXML private Button buttonReset;
    @FXML private Slider tempSlider;
    @FXML private TextField tempTextField;
    @FXML private Slider ottSlider;
    @FXML private TextField ottTextField;

    @FXML private Slider expSlider;
    @FXML private TextField expTextField;
    @FXML private Slider contrSlider;
    @FXML private TextField contrTextField;
    @FXML private Slider lightSlider;
    @FXML private TextField lightTextField;
    @FXML private Slider shadeSlider;
    @FXML private TextField shadeTextField;

    @FXML private ImageView imageView;

    private static int SLIDER_WORDS_COUNT = 6;

    @FXML
    private void tempSliderDrag(Event event) {
        sliderDrag(event);
    }

    @FXML
    private void ottSliderDrag(Event event) {
        sliderDrag(event);
    }
    
    @FXML
    private void expSliderDrag(Event event) {
        sliderDrag(event);
    }

    @FXML
    private void contrSliderDrag(Event event) {
        sliderDrag(event);
    }

    private void sliderDrag(Event event) {
        String sliderId = getId(event);
        String textFieldId =
                sliderId.substring(0, sliderId.length() - SLIDER_WORDS_COUNT) + "TextField";

        Scene scene  = imageView.getScene();
        Slider slider = (Slider) scene.lookup(sliderId);
        TextField textField = (TextField) scene.lookup(textFieldId);

        String value = String.valueOf(Math.round(slider.getValue()));

        textField.setText(value);
    }

    private String getId(Event event) {
        final Node source = (Node) event.getSource();
        return "#" + source.getId();
    }
}

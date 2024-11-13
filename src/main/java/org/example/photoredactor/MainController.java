package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.photoredactor.WB.TintSetting;
import org.example.photoredactor.presence.SaturationSetting;
import org.example.photoredactor.presence.VibranceSetting;
import org.example.photoredactor.settings.Settings;
import org.example.photoredactor.tone.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainController {
    private TintSetting tintSetting = new TintSetting();

    private ExposeSetting exposeSetting = new ExposeSetting();
    private ContrSetting contrSetting = new ContrSetting();
    private LightsSetting lightsSetting = new LightsSetting();
    private ShadowsSetting shadowsSetting = new ShadowsSetting();
    private WhitesSetting whitesSetting = new WhitesSetting();
    private BlacksSetting blacksSetting = new BlacksSetting();

    private SaturationSetting saturSetting = new SaturationSetting();
    private VibranceSetting vibrSetting = new VibranceSetting();

    private final Map<String, Settings> settingsMap = new HashMap<>(Map.ofEntries(
            Map.entry("#expSlider", exposeSetting),
            Map.entry("#expTextField", exposeSetting),
            Map.entry("#contrSlider", contrSetting),
            Map.entry("#contrTextField", contrSetting),
            Map.entry("#lightsSlider", lightsSetting),
            Map.entry("#lightsTextField", lightsSetting),
            Map.entry("#shadowsSlider", shadowsSetting),
            Map.entry("#shadowsTextField", shadowsSetting),
            Map.entry("#whitesSlider", whitesSetting),
            Map.entry("#whitesTextField", whitesSetting),
            Map.entry("#blacksSlider", blacksSetting),
            Map.entry("#blacksTextField", blacksSetting),
            Map.entry("#saturSlider", saturSetting),
            Map.entry("#saturTextField", saturSetting),
            Map.entry("#vibrSlider", vibrSetting),
            Map.entry("#vibrTextField", vibrSetting),
            Map.entry("#tintSlider", tintSetting),
            Map.entry("#tintTextField", tintSetting)
    ));

    @FXML private ImageView imageView;

    private static String SLIDER_STYLE_CLASS = "slider";
    private static String TEXT_FIELD_STYLE_CLASS = "text-input text-field";

    @FXML
    private void onSliderDrag(Event event) {
        TextSliderConnect.sliderDrag(event, imageView);
    }

    @FXML
    private void onTextFieldEdit(Event event) {
        TextSliderConnect.textFieldEdit(event, imageView);
    }

    @FXML void changeSetting(Event event) {
        double coef = 0;
        Settings setting = null;

        String id = Calculator.getId(event);
        final Node source = (Node) event.getSource();
        String sourceClass = source.getStyleClass().toString();

        if (sourceClass.equals(SLIDER_STYLE_CLASS)) {
            Slider slider = (Slider) event.getSource();
            setting = settingsMap.get(id);
            coef = Math.round(slider.getValue());
        } else if (sourceClass.equals(TEXT_FIELD_STYLE_CLASS)) {
            TextField textField = (TextField) event.getSource();
            setting = settingsMap.get(id);
            coef = Double.parseDouble(textField.getText());
        }

        changeImage(setting, coef);
    }

    @FXML
    private void changeImage(Settings setting, double coef) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = imread("src/main/resources/org/example/photoredactor/IMG_6374.jpg");

        Calculator.changeImage(image, setting, coef);

        imwrite("src/main/resources/org/example/photoredactor/IMG_6374_new.jpg", image);

        File file = new File("src/main/resources/org/example/photoredactor/IMG_6374_new.jpg");
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }
}
package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.photoredactor.WB.TempSetting;
import org.example.photoredactor.WB.TintSetting;
import org.example.photoredactor.presence.detail.ClaritySetting;
import org.example.photoredactor.presence.detail.BlurSetting;
import org.example.photoredactor.presence.detail.SharpeningSetting;
import org.example.photoredactor.presence.color.SaturationSetting;
import org.example.photoredactor.presence.color.VibranceSetting;
import org.example.photoredactor.settings.Setting;
import org.example.photoredactor.tone.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainController {
    private TempSetting tempSetting = new TempSetting();
    private TintSetting tintSetting = new TintSetting();

    private ExposeSetting exposeSetting = new ExposeSetting();
    private ContrSetting contrSetting = new ContrSetting();
    private LightsSetting lightsSetting = new LightsSetting();
    private ShadowsSetting shadowsSetting = new ShadowsSetting();
    private WhitesSetting whitesSetting = new WhitesSetting();
    private BlacksSetting blacksSetting = new BlacksSetting();

    private SaturationSetting saturSetting = new SaturationSetting();
    private VibranceSetting vibrSetting = new VibranceSetting();
    private SharpeningSetting sharpeningSetting = new SharpeningSetting();
    private BlurSetting blurSetting = new BlurSetting();
    private ClaritySetting claritySetting = new ClaritySetting();

    private final Map<String, Setting> settingsMap = new HashMap<>(Map.ofEntries(
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
            Map.entry("#tempSlider", tempSetting),
            Map.entry("#tempTextField", tempSetting),
            Map.entry("#tintSlider", tintSetting),
            Map.entry("#tintTextField", tintSetting),
            Map.entry("#sharpeningSlider", sharpeningSetting),
            Map.entry("#sharpeningTextField", sharpeningSetting),
            Map.entry("#blurSlider", blurSetting),
            Map.entry("#blurTextField", blurSetting),
            Map.entry("#claritySlider", claritySetting),
            Map.entry("#clarityTextField", claritySetting)
    ));

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML private ImageView imageView;
    private String curImageFileName;

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
        Setting setting = null;

        String id = Helper.getId(event);
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
    private void changeImage(Setting setting, double coef) {
        Mat curImage = imread(curImageFileName);
        Helper.changeImage(curImage, setting, coef);

        imwrite("src/main/resources/org/example/photoredactor/test.jpg", curImage);

        File file = new File("src/main/resources/org/example/photoredactor/test.jpg");
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    // Пока не дружит с пробелами и русскими символами
    @FXML
    private void openFiles() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        Image img = new Image(file.toURI().toString());
        curImageFileName = file.toString().replace('\\', '/');
        imageView.setImage(img);
    }

    @FXML
    private void resetSettings() {
        Setting.resetSettings();
        changeImage(exposeSetting, 1);

        Scene scene = imageView.getScene();
        for (String id : settingsMap.keySet()) {
            if (id.endsWith("Slider")) {
                Slider slider = (Slider) scene.lookup(id);
                slider.setValue(0);
            } else if (id.endsWith("TextField")) {
                TextField textField = (TextField) scene.lookup(id);
                textField.setText("0");
            }
        }
    }

    public MainController() {
        Setting.initSettings(settingsMap.values());
    }
}
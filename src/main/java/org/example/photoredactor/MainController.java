package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainController {
    //to-do подключить нормальный логгер
    protected static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

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

    @FXML private ImageView imageView;
    private String curImageFileName;
    private String curImageEditCopy;

    private List<String> originalImages = new ArrayList<>();
    private List<String> editingCopyImages = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static String SLIDER_STYLE_CLASS = "slider";
    private static String TEXT_FIELD_STYLE_CLASS = "text-input text-field";

    @FXML private void onSliderDrag(Event event) {
        TextSliderConnect.sliderDrag(event, imageView);
    }

    @FXML private void onTextFieldEdit(Event event) {
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

        changeImage(curImageFileName, curImageEditCopy, setting, coef);
    }

    @FXML void changeAllImages() {
        changeAllImages(exposeSetting, exposeSetting.getCoef());
    }

    private void changeImage(String image, String imageEditCopy, Setting setting, double coef) {
        Mat curImage = imread(image);
        Helper.changeImage(curImage, setting, coef);

        imwrite(imageEditCopy, curImage);

        File file = new File(imageEditCopy);
        Image img = new Image(file.toURI().toString());

        int ind = originalImages.indexOf(image);
        imageViews.get(ind).setImage(img);
    }

    private void changeAllImages(Setting setting, double coef) {
        for (int ind = 0; ind < originalImages.size(); ++ind) {
            changeImage(
                    originalImages.get(ind),
                    editingCopyImages.get(ind),
                    setting, // монжо подставить любую другую настройку
                    coef);
        }
    }

    // Пока не дружит с пробелами и русскими символами
    @FXML private void openFiles() throws IOException {
        Scene scene = imageView.getScene();
        HBox imagesList = (HBox) scene.lookup("#imagesList");

        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(imageView.getScene().getWindow());

        if (files != null) {
            clear();

            for (File file : files) {
                Image img = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(100);
                imageView.setFitHeight(75);
                imageView.setOnMouseClicked(this::setCurImage);
                imagesList.getChildren().add(imageView);
                imageViews.add(imageView);

                String curFileName = Helper.fileToString(file);
                String copyFileName = "src/main/resources/org/example/photoredactor/"
                        + Helper.getImgName(curFileName);
                originalImages.add(curFileName);
                editingCopyImages.add(copyFileName);

                Mat curImage = imread(curFileName); //to-do переписать под Files.copy()
                imwrite(copyFileName, curImage);
            }

            Image img = new Image(files.getFirst().toURI().toString());
            curImageFileName = originalImages.getFirst();
            curImageEditCopy = editingCopyImages.getFirst();

            imageView.setImage(img);
            imageView.setFitWidth(1030);
        }
    }

    @FXML void saveFile() {
        saveFilesToDirectory(List.of(curImageEditCopy));
    }

    @FXML void saveAllFiles() {
        saveFilesToDirectory(editingCopyImages);
    }

    @FXML void saveFilesToDirectory(List<String> files) {
        File directory = getDirectory();

        for (String fileName : files) {
            String saveTo = Helper.fileToString(directory)
                    + "/" + Helper.getImgName(fileName);
            Mat curImage = imread(fileName);
            imwrite(saveTo, curImage);
        }
    }

    @FXML private void resetSettings() {
        Setting.resetSettings();

        changeImage(curImageFileName, curImageEditCopy, exposeSetting, 1); // монжо подставить любую другую настройку вместо expose

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

    private void setCurImage(MouseEvent event) {
        ImageView newImageView = (ImageView) event.getSource();
        int ind = imageViews.indexOf(newImageView);

        curImageFileName = originalImages.get(ind);
        curImageEditCopy = editingCopyImages.get(ind);

        File file = new File(curImageEditCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    private File getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Scene scene = imageView.getScene();
        return directoryChooser.showDialog(scene.getWindow());
    }

    public MainController() {
        Setting.initSettings(settingsMap.values());
    }

    public void clear() {
        originalImages.clear();
        editingCopyImages.clear();
        imageViews.clear();

        Scene scene = imageView.getScene();
        HBox imagesList = (HBox) scene.lookup("#imagesList");
        imagesList.getChildren().clear();
    }
}
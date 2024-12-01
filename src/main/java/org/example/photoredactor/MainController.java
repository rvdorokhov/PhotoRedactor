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
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainController {
    //to-do подключить нормальный логгер
    // protected static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML private ImageView imageView;

    private List<EditingImage> editingImages = new ArrayList<>();
    private EditingImage curImage;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static String SLIDER_STYLE_CLASS = "slider";
    private static String TEXT_FIELD_STYLE_CLASS = "text-input text-field";
//
    @FXML private void onSliderDrag(Event event) {
        TextSliderConnect.sliderDrag(event, imageView);
    }
//
    @FXML private void onTextFieldEdit(Event event) {
        TextSliderConnect.textFieldEdit(event, imageView);
    }
//
    @FXML void changeSetting(Event event) {
        double coef = 0;

        String id = Helper.getId(event);
        final Node source = (Node) event.getSource();
        String sourceClass = source.getStyleClass().toString();

        if (sourceClass.equals(SLIDER_STYLE_CLASS)) {
            Slider slider = (Slider) event.getSource();
            coef = Math.round(slider.getValue());
        } else if (sourceClass.equals(TEXT_FIELD_STYLE_CLASS)) {
            TextField textField = (TextField) event.getSource();
            coef = Double.parseDouble(textField.getText());
        }

        changeImage(curImage, id, coef);
    }
//
    @FXML void changeAllImages() {
        for (EditingImage image : editingImages) {
            if (image != curImage) {
                image.changeSettings(curImage.settingsMap);
                image.changeImage();
            }
        }
    }
//
    private void changeImage(EditingImage editingImage, String settingName, double coef) {
        editingImage.changeImage(settingName, coef);

        String imageEditCopy = editingImage.editingCopy;
        File file = new File(imageEditCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }
//
    public void addImagesToList(List<File> files) {
        Scene scene = imageView.getScene();
        HBox imagesList = (HBox) scene.lookup("#imagesList");

        if (files != null) {
            clear();

            for (File file : files) {
                Image img = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(100);
                imageView.setFitHeight(75);
                imageView.setOnMouseClicked(this::setCurImage);
                imagesList.getChildren().add(imageView);

                String curFileName = Helper.fileToString(file);
                EditingImage image = new EditingImage(curFileName, imageView);
                editingImages.add(image);
            }

            Image img = new Image(files.getFirst().toURI().toString());
            curImage = editingImages.getFirst();

            imageView.setImage(img);
            imageView.setFitWidth(1030);
        }
    }

    // Пока не дружит с пробелами и русскими символами
    @FXML private void openFiles() throws IOException {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(imageView.getScene().getWindow());

        addImagesToList(files);
    }
//
    @FXML void saveFile() {
        saveFilesToDirectory(List.of(curImage.editingCopy));
    }
//
    @FXML void saveAllFiles() {
        List<String> editingCopyImages = new ArrayList<>();
        editingImages.forEach(editingImage ->
                editingCopyImages.add(editingImage.editingCopy));
        saveFilesToDirectory(editingCopyImages);
    }
//
    @FXML void saveFilesToDirectory(List<String> files) {
        File directory = getDirectory();

        for (String fileName : files) {
            String saveTo = Helper.fileToString(directory)
                    + "/" + Helper.getImgName(fileName);
            Mat curImage = imread(fileName);
            imwrite(saveTo, curImage);
        }
    }
//
    @FXML private void resetSettings() {
        curImage.resetSettings();

        changeImage(curImage, "#expSlider", 1); // можно подставить любую другую настройку вместо expose

        Scene scene = imageView.getScene();
        for (String id : curImage.settingsMap.keySet()) {
            if (id.endsWith("Slider")) {
                Slider slider = (Slider) scene.lookup(id);
                slider.setValue(0);
            } else if (id.endsWith("TextField")) {
                TextField textField = (TextField) scene.lookup(id);
                textField.setText("0");
            }
        }
    }
//
    private void setCurImage(MouseEvent event) {
        ImageView newImageView = (ImageView) event.getSource();
        EditingImage editingImage = findEditingImage(newImageView);

        curImage = editingImage;

        setImageToImageView(editingImage);
    }
//
    private void setImageToImageView(EditingImage editingImage) {
        File file = new File(editingImage.editingCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }
//
    private File getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Scene scene = imageView.getScene();
        return directoryChooser.showDialog(scene.getWindow());
    }
//
    public void clear() {
        editingImages.clear();

        Scene scene = imageView.getScene();
        HBox imagesList = (HBox) scene.lookup("#imagesList");
        imagesList.getChildren().clear();
    }
//
    public EditingImage findEditingImage(ImageView imageView) {
        for (EditingImage image : editingImages) {
            if (image.imageView == imageView) {
                return image;
            }
        }

        return null;
    }
}
package org.example.photoredactor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    private static String SLIDER_STYLE_CLASS = "slider";
    private static String TEXT_FIELD_STYLE_CLASS = "text-input text-field";

    @FXML public void onSliderDrag(Event event) {
        TextSliderConnect.sliderDrag(event, imageView);
    }

    @FXML public void onTextFieldEdit(Event event) {
        TextSliderConnect.textFieldEdit(event, imageView);
    }

    @FXML public void changeSetting(Event event) {
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
            if (!Helper.isValueCorrect(coef, event)) {
                coef = 0;
            }
        }

        changeImage(curImage, id, coef);
    }

    @FXML public void changeAllImages() {
        for (EditingImage image : editingImages) {
            if (image != curImage) {
                image.changeSettings(curImage.settingsMap);
                image.changeImage();
            }
        }
    }

    private void changeImage(EditingImage editingImage, String settingName, double coef) {
        editingImage.changeImage(settingName, coef);

        String imageEditCopy = editingImage.editingCopy;
        File file = new File(imageEditCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    private void addImagesToList(List<File> files) {
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

                EditingImage image = null;
                try {
                    image = new EditingImage(curFileName, imageView);
                    editingImages.add(image);
                } catch (Exception e) {
                    clear();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Неподдерживаемый формат файла");
                    alert.setContentText("Выбран неподдерживаемый формат изображения.");
                    alert.showAndWait();
                }
            }

            Image img = new Image(files.getFirst().toURI().toString());
            curImage = editingImages.getFirst();

            imageView.setImage(img);
            imageView.setFitWidth(1030);
        }
    }

    // Пока не дружит с пробелами и русскими символами
    @FXML public void openFiles() throws IOException {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(imageView.getScene().getWindow());

        addImagesToList(files);
    }

    @FXML public void saveFile() {
        saveFilesToDirectory(List.of(curImage.editingCopy));
    }

    @FXML public void saveAllFiles() {
        List<String> editingCopyImages = new ArrayList<>();
        editingImages.forEach(editingImage ->
                editingCopyImages.add(editingImage.editingCopy));
        saveFilesToDirectory(editingCopyImages);
    }

    @FXML public void saveFilesToDirectory(List<String> files) {
        File directory = getDirectory();

        for (String fileName : files) {
            String saveTo = Helper.fileToString(directory)
                    + "/" + Helper.getImgName(fileName);
            Mat curImage = imread(fileName);
            imwrite(saveTo, curImage);
        }
    }

    @FXML public void resetSettings() {
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

    private String extractModelToTempFile() throws Exception {
        try (var is = MainApplication.class.getResourceAsStream("/models/iq_multihd_4heads.onnx")) {
            if (is == null) {
                throw new RuntimeException("Модель не найдена в resources: /models/iq_multihd_4heads.onnx");
            }

            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("iq_multihd_4heads_", ".onnx");
            java.nio.file.Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();

            return tempFile.toAbsolutePath().toString();
        }
    }

    @FXML
    public void autoSelection() {
        if (editingImages == null || editingImages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Автоотбор", "Нет изображений", "Сначала загрузите изображения.");
            return;
        }

        try {
            // путь к ONNX-модели
            String modelPath = extractModelToTempFile();

            // пороги
            AutoSelector selector = new AutoSelector(
                    modelPath,
                    0.5f, // blur
                    0.5f, // under
                    0.5f, // over
                    0.5f  // night
            );

            List<EditingImage> approved = new ArrayList<>();
            List<Node> approvedNodes = new ArrayList<>();

            for (EditingImage img : editingImages) {
                boolean ok = selector.approve(img.original);
                if (ok) {
                    approved.add(img);
                    approvedNodes.add(img.imageView);
                }
            }

            selector.close();

            Scene scene = imageView.getScene();
            HBox imagesList = (HBox) scene.lookup("#imagesList");

            editingImages.clear();
            editingImages.addAll(approved);

            imagesList.getChildren().clear();
            imagesList.getChildren().addAll(approvedNodes);

            if (editingImages.isEmpty()) {
                curImage = null;
                imageView.setImage(null);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Автоотбор",
                        "Готово",
                        "Нейронная сеть не одобрила ни одного изображения."
                );
                return;
            }

            curImage = editingImages.getFirst();
            setImageToImageView(curImage);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Автоотбор",
                    "Готово",
                    "Оставлено изображений: " + editingImages.size()
            );

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Ошибка",
                    "Автоотбор не выполнен",
                    e.getMessage()
            );
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setCurImage(MouseEvent event) {
        ImageView newImageView = (ImageView) event.getSource();
        EditingImage editingImage = findEditingImage(newImageView);

        curImage = editingImage;

        setImageToImageView(editingImage);

        Scene scene = imageView.getScene();
        TextField tempTextField = (TextField) scene.lookup("#tempTextField");
        Slider tempSlider = (Slider) scene.lookup("#tempSlider");
        tempTextField.setText(String.valueOf(Math.round(curImage.tempSetting.getCoef() * 100) - 100));
        tempSlider.setValue(curImage.tempSetting.getCoef() * 100 - 100);

        TextField tintTextField = (TextField) scene.lookup("#tintTextField");
        Slider tintSlider = (Slider) scene.lookup("#tintSlider");
        tintTextField.setText(String.valueOf(Math.round(curImage.tintSetting.getCoef() * 100) - 100));
        tintSlider.setValue(curImage.tintSetting.getCoef() * 100 - 100);

        TextField expTextField = (TextField) scene.lookup("#expTextField");
        Slider expSlider = (Slider) scene.lookup("#expSlider");
        expTextField.setText(String.valueOf(Math.round(curImage.exposeSetting.getCoef() * 100) - 100));
        expSlider.setValue(curImage.exposeSetting.getCoef() * 100 - 100);

        TextField contrTextField = (TextField) scene.lookup("#contrTextField");
        Slider contrSlider = (Slider) scene.lookup("#contrSlider");
        contrTextField.setText(String.valueOf(Math.round(curImage.contrSetting.getCoef() * 100) - 100));
        contrSlider.setValue(curImage.contrSetting.getCoef() * 100 - 100);

        TextField lightsTextField = (TextField) scene.lookup("#lightsTextField");
        Slider lightsSlider = (Slider) scene.lookup("#lightsSlider");
        lightsTextField.setText(String.valueOf(Math.round(curImage.lightsSetting.getCoef() * 100) - 100));
        lightsSlider.setValue(curImage.lightsSetting.getCoef() * 100 - 100);

        TextField shadowsTextField = (TextField) scene.lookup("#shadowsTextField");
        Slider shadowsSlider = (Slider) scene.lookup("#shadowsSlider");
        shadowsTextField.setText(String.valueOf(Math.round(curImage.shadowsSetting.getCoef() * 100) - 100));
        shadowsSlider.setValue(curImage.shadowsSetting.getCoef() * 100 - 100);

        TextField whitesTextField = (TextField) scene.lookup("#whitesTextField");
        Slider whitesSlider = (Slider) scene.lookup("#whitesSlider");
        whitesTextField.setText(String.valueOf(Math.round(curImage.whitesSetting.getCoef() * 100) - 100));
        whitesSlider.setValue(curImage.whitesSetting.getCoef() * 100 - 100);

        TextField blacksTextField = (TextField) scene.lookup("#blacksTextField");
        Slider blacksSlider = (Slider) scene.lookup("#blacksSlider");
        blacksTextField.setText(String.valueOf(Math.round(curImage.blacksSetting.getCoef() * 100) - 100));
        blacksSlider.setValue(curImage.blacksSetting.getCoef() * 100 - 100);

        TextField sharpeningTextField = (TextField) scene.lookup("#sharpeningTextField");
        Slider sharpeningSlider = (Slider) scene.lookup("#sharpeningSlider");
        sharpeningTextField.setText(String.valueOf(Math.round(curImage.sharpeningSetting.getCoef() * 100)));
        sharpeningSlider.setValue(curImage.sharpeningSetting.getCoef() * 100);

        TextField clarityTextField = (TextField) scene.lookup("#clarityTextField");
        Slider claritySlider = (Slider) scene.lookup("#claritySlider");
        clarityTextField.setText(String.valueOf(Math.round(curImage.claritySetting.getCoef() * 100) - 100));
        claritySlider.setValue(curImage.claritySetting.getCoef() * 100 - 100);

        TextField blurTextField = (TextField) scene.lookup("#blurTextField");
        Slider blurSlider = (Slider) scene.lookup("#blurSlider");
        blurTextField.setText(String.valueOf(Math.round(curImage.blurSetting.getCoef() * 100)));
        blurSlider.setValue(curImage.blurSetting.getCoef() * 100);

        TextField vibrTextField = (TextField) scene.lookup("#vibrTextField");
        Slider vibrSlider = (Slider) scene.lookup("#vibrSlider");
        vibrTextField.setText(String.valueOf(Math.round(curImage.vibrSetting.getCoef() * 100) - 100));
        vibrSlider.setValue(curImage.vibrSetting.getCoef() * 100 - 100);

        TextField saturTextField = (TextField) scene.lookup("#saturTextField");
        Slider saturSlider = (Slider) scene.lookup("#saturSlider");
        saturTextField.setText(String.valueOf(Math.round(curImage.saturSetting.getCoef() * 100) - 100));
        saturSlider.setValue(curImage.saturSetting.getCoef() * 100 - 100);
    }

    private void setImageToImageView(EditingImage editingImage) {
        File file = new File(editingImage.editingCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    private File getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Scene scene = imageView.getScene();
        return directoryChooser.showDialog(scene.getWindow());
    }

    private void clear() {
        editingImages.clear();

        Scene scene = imageView.getScene();
        HBox imagesList = (HBox) scene.lookup("#imagesList");
        imagesList.getChildren().clear();
    }

    private EditingImage findEditingImage(ImageView imageView) {
        for (EditingImage image : editingImages) {
            if (image.imageView == imageView) {
                return image;
            }
        }

        return null;
    }
}
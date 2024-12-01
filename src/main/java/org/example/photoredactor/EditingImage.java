package org.example.photoredactor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.photoredactor.WB.TempSetting;
import org.example.photoredactor.WB.TintSetting;
import org.example.photoredactor.presence.color.SaturationSetting;
import org.example.photoredactor.presence.color.VibranceSetting;
import org.example.photoredactor.presence.detail.BlurSetting;
import org.example.photoredactor.presence.detail.ClaritySetting;
import org.example.photoredactor.presence.detail.SharpeningSetting;
import org.example.photoredactor.settings.EditSetting;
import org.example.photoredactor.settings.Setting;
import org.example.photoredactor.tone.*;
import org.opencv.core.Mat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

//to-do добавить ломбок
public class EditingImage {
    public String original;
    public String editingCopy;
    public String editingCopyRel;
    public ImageView imageView;

    public TempSetting tempSetting = new TempSetting();
    public TintSetting tintSetting = new TintSetting();

    public ExposeSetting exposeSetting = new ExposeSetting();
    public ContrSetting contrSetting = new ContrSetting();
    public LightsSetting lightsSetting = new LightsSetting();
    public ShadowsSetting shadowsSetting = new ShadowsSetting();
    public WhitesSetting whitesSetting = new WhitesSetting();
    public BlacksSetting blacksSetting = new BlacksSetting();

    public SaturationSetting saturSetting = new SaturationSetting();
    public VibranceSetting vibrSetting = new VibranceSetting();
    public SharpeningSetting sharpeningSetting = new SharpeningSetting();
    public BlurSetting blurSetting = new BlurSetting();
    public ClaritySetting claritySetting = new ClaritySetting();

    private EditSetting editSetting = new EditSetting();

    public final Map<String, Setting> settingsMap = new HashMap<>(Map.ofEntries(
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

    private static final String WORKING_DIRECTORY =
            "src/main/resources/org/example/photoredactor/";
    private static final String REL_WORKING_DIRECTORY =
            "/org/example/photoredactor/";

    public void changeImage() {
        Mat matImage = imread(original);
        editSetting.changeImage(matImage);
        imwrite(editingCopy, matImage);

        File file = new File(editingCopy);
        Image img = new Image(file.toURI().toString());
        imageView.setImage(img);
    }

    public void changeImage(String settingName, Double coef) {
        settingsMap.get(settingName).setCoef(coef);
        changeImage();
    }

    public void changeSettings(Map<String, Setting> newSettingsMap) {
        for (String key : newSettingsMap.keySet()) {
            double newCoef = newSettingsMap.get(key).getCoef();
            settingsMap.get(key).setCoefWithoutConvertation(newCoef);
        }
    }

    public void resetSettings() {
        editSetting.resetSettings();
    }

    public EditingImage(String original, ImageView imageView) {
        this.original = original;
        editingCopy = WORKING_DIRECTORY + Helper.getImgName(original);
        editingCopyRel = REL_WORKING_DIRECTORY + Helper.getImgName(original);
        this.imageView = imageView;

        editSetting.initSettings(settingsMap.values());

        Mat image = imread(original); //to-do переписать под Files.copy()
        imwrite(editingCopy, image);
    }
}

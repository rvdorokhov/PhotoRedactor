package org.example.photoredactor.tone;

import org.example.photoredactor.settings.SettingsWithSameCoefs;

public class ExposeSetting extends SettingsWithSameCoefs {
    // здесь changeRGB в констуркторе,
    // потому что оно не изменяется ни при каких внешних условиях и коэффициентх
    public ExposeSetting() {
        changeRGB = (color, coef) -> Math.pow(color/255.0, (2 - coef)) * 255;
    }
}
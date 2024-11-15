package org.example.photoredactor.settings;

import org.opencv.core.Core;

public abstract class SettingsImageEdit extends Settings {
    static {
        // Загрузка библиотеки OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}

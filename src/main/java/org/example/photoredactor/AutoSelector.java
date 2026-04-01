package org.example.photoredactor;

import ai.onnxruntime.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class AutoSelector implements AutoCloseable {
    private static final int IMG_H = 384;
    private static final int IMG_W = 512;

    private final OrtEnvironment env;
    private final OrtSession session;

    private final float thrBlur;
    private final float thrUnder;
    private final float thrOver;
    private final float thrNight;

    public AutoSelector(String modelPath,
                        float thrBlur,
                        float thrUnder,
                        float thrOver,
                        float thrNight) throws OrtException {
        this.env = OrtEnvironment.getEnvironment();

        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        this.session = env.createSession(modelPath, opts);

        // ОТЛАДКА

        System.out.println("Model path: " + modelPath);
        System.out.println("Exists: " + new java.io.File(modelPath).exists());
        System.out.println("Input info: " + session.getInputInfo());
        System.out.println("Output info: " + session.getOutputInfo());

        // ОТЛАДКА

        this.thrBlur = thrBlur;
        this.thrUnder = thrUnder;
        this.thrOver = thrOver;
        this.thrNight = thrNight;
    }

    public boolean approve(String imagePath) throws Exception {
        float[] inputData = preprocess(imagePath);

        String inputName = session.getInputNames().iterator().next();

        try (OnnxTensor inputTensor = OnnxTensor.createTensor(
                env,
                FloatBuffer.wrap(inputData),
                new long[]{1, IMG_H, IMG_W, 3}
        )) {
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);

            try (OrtSession.Result results = session.run(inputs)) {
                Object value = results.get(0).getValue();

                float[] logits = extractLogits(value);
                if (logits.length != 4) {
                    throw new RuntimeException("Ожидалось 4 выхода модели, получено: " + logits.length);
                }

                float pBlur = sigmoid(logits[0]);
                float pUnder = sigmoid(logits[1]);
                float pOver = sigmoid(logits[2]);
                float pNight = sigmoid(logits[3]);

                System.out.println("Logits length: " + logits.length);
                System.out.println("Logits: " + java.util.Arrays.toString(logits));

                System.out.println("Probs: blur=" + pBlur +
                        ", under=" + pUnder +
                        ", over=" + pOver +
                        ", night=" + pNight);

                return pBlur < thrBlur
                        && pUnder < thrUnder
                        && pOver < thrOver
                        && pNight < thrNight;
            }
        }
    }

    private float[] preprocess(String imagePath) {
        Mat img = imread(imagePath);
        if (img.empty()) {
            throw new RuntimeException("Не удалось загрузить изображение: " + imagePath);
        }

        Mat rgb = new Mat();
        Imgproc.cvtColor(img, rgb, Imgproc.COLOR_BGR2RGB);

        Mat resized = new Mat();
        Imgproc.resize(rgb, resized, new org.opencv.core.Size(IMG_W, IMG_H));

        Mat floatMat = new Mat();
        resized.convertTo(floatMat, CvType.CV_32FC3);

        int total = (int) (floatMat.total() * floatMat.channels());
        float[] chw = new float[total];
        floatMat.get(0, 0, chw);

        return chw; // NHWC: [1, 384, 512, 3]
    }

    private float[] extractLogits(Object value) {
        if (value instanceof float[][] arr2) {
            return arr2[0];
        }
        if (value instanceof float[] arr1) {
            return arr1;
        }
        throw new RuntimeException("Неожиданный тип выхода модели: " + value.getClass());
    }

    private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }

    @Override
    public void close() throws OrtException {
        session.close();
    }
}
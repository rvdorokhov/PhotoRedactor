package org.example.photoredactor;

import ai.onnxruntime.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.*;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class AutoSelector implements AutoCloseable {
    private static final int IMG_H = 384;
    private static final int IMG_W = 512;

    private static final int PATCH_ROWS = 3;
    private static final int PATCH_COLS = 3;
    private static final int BLUR_TOP_K = 3; // усредняем 3 худших фрагмента

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

        this.thrBlur = thrBlur;
        this.thrUnder = thrUnder;
        this.thrOver = thrOver;
        this.thrNight = thrNight;
    }

    public boolean approve(String imagePath) throws Exception {
        Mat image = imread(imagePath);
        if (image.empty()) {
            throw new RuntimeException("Не удалось загрузить изображение: " + imagePath);
        }

        // 1) Полный кадр -> under / over / night
        float[] fullLogits = runInference(image);

        float pUnder = sigmoid(fullLogits[1]);
        float pOver = sigmoid(fullLogits[2]);
        float pNight = sigmoid(fullLogits[3]);

        // 2) Blur по 9 фрагментам
        // List<Float> blurPatchProbs = evaluateBlurByPatches(image);
        // float blurScore = aggregateBlur(blurPatchProbs);
        float blurScore = evaluateBlurWholeImage(image);

        System.out.println(imagePath);
        System.out.println("Full probs: under=" + pUnder +
                ", over=" + pOver +
                ", night=" + pNight);
        // System.out.println("Blur patch probs: " + blurPatchProbs);
        System.out.println("Blur aggregated: " + blurScore);

        boolean blurOk = blurScore < thrBlur;
        boolean isNight = pNight >= thrNight;
        boolean overOk = pOver < thrOver;
        boolean underOk = isNight || (pUnder < thrUnder);

        return blurOk && overOk && underOk;
    }

    private List<Float> evaluateBlurByPatches(Mat image) throws Exception {
        List<Float> blurProbs = new ArrayList<>(PATCH_ROWS * PATCH_COLS);

        int rows = image.rows();
        int cols = image.cols();

        for (int r = 0; r < PATCH_ROWS; r++) {
            int y0 = r * rows / PATCH_ROWS;
            int y1 = (r + 1) * rows / PATCH_ROWS;

            for (int c = 0; c < PATCH_COLS; c++) {
                int x0 = c * cols / PATCH_COLS;
                int x1 = (c + 1) * cols / PATCH_COLS;

                int w = x1 - x0;
                int h = y1 - y0;

                if (w <= 0 || h <= 0) {
                    continue;
                }

                Rect rect = new Rect(x0, y0, w, h);
                Mat patch = new Mat(image, rect).clone();

                float[] logits = runInference(patch);
                float pBlur = sigmoid(logits[0]);
                blurProbs.add(pBlur);
            }
        }

        if (blurProbs.isEmpty()) {
            throw new RuntimeException("Не удалось получить ни одного фрагмента для blur.");
        }

        return blurProbs;
    }

    private float evaluateBlurWholeImage(Mat image) throws OrtException {
        float[] logits = runInference(image);
        return sigmoid(logits[0]);
    }

    private float aggregateBlur(List<Float> blurProbs) {
        if (blurProbs.size() != PATCH_ROWS * PATCH_COLS) {
            throw new RuntimeException(
                    "Ожидалось " + (PATCH_ROWS * PATCH_COLS) +
                            " blur-оценок, получено: " + blurProbs.size()
            );
        }

        final float[][] weights = {
                {0.5f, 0.7f, 0.5f},
                {0.8f, 1.8f, 0.8f},
                {0.7f, 1.4f, 0.7f}
        };

        float weightedSum = 0f;
        float weightSum = 0f;

        int idx = 0;
        for (int r = 0; r < PATCH_ROWS; r++) {
            for (int c = 0; c < PATCH_COLS; c++) {
                float prob = blurProbs.get(idx++);
                float w = weights[r][c];

                weightedSum += prob * w;
                weightSum += w;
            }
        }

        return weightedSum / weightSum;
    }

    private float[] runInference(Mat image) throws OrtException {
        float[] inputData = preprocess(image);

        String inputName = session.getInputNames().iterator().next();

        try (OnnxTensor inputTensor = OnnxTensor.createTensor(
                env,
                FloatBuffer.wrap(inputData),
                new long[]{1, IMG_H, IMG_W, 3}
        )) {
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);

            try (OrtSession.Result results = session.run(inputs)) {
                Object value = results.get(0).getValue();
                return extractLogits(value);
            }
        }
    }

    private float[] preprocess(Mat bgrImage) {
        Mat rgb = new Mat();
        Imgproc.cvtColor(bgrImage, rgb, Imgproc.COLOR_BGR2RGB);

        Mat resized = new Mat();
        Imgproc.resize(rgb, resized, new Size(IMG_W, IMG_H));

        Mat floatMat = new Mat();
        resized.convertTo(floatMat, CvType.CV_32FC3);

        int total = (int) (floatMat.total() * floatMat.channels());
        float[] data = new float[total];
        floatMat.get(0, 0, data);

        return data; // NHWC
    }

    private float[] extractLogits(Object value) {
        if (value instanceof float[][] arr2) {
            float[] logits = arr2[0];
            return logits;
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
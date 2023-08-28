package com.example.g29.msbandapp;
import android.content.Context;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class ActivityClassifier {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/frozen_har.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODES = {"y_"};
    private static final String OUTPUT_NODE = "y_";
    private static final long[] INPUT_SIZE = {1, 5, 6};
    private static final int OUTPUT_SIZE = 5;

    public ActivityClassifier(final Context context){
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    public float[] predictActivityProbabilities(float[] predictionData){
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, predictionData, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        return result;
    }

}

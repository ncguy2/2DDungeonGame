package net.ncguy.lib.foundation.collections;

import java.util.function.BiFunction;

public class LambdaPredictionQueue<T> extends PredictionQueue<T> {

    private final BiFunction<PredictionQueue<T>, Float, T> predictionFunc;

    public LambdaPredictionQueue(int maxSize, BiFunction<PredictionQueue<T>, Float, T> predictionFunc) {
        super(maxSize);
        this.predictionFunc = predictionFunc;
    }

    @Override
    public T Predict(float futureTime) {
        return predictionFunc.apply(this, futureTime);
    }
}

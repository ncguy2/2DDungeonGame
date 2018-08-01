package net.ncguy.lib.foundation.collections;

public abstract class PredictionQueue<T> extends HistoryQueue<T> {

    public PredictionQueue(int maxSize) {
        super(maxSize);
    }

    public abstract T Predict(float futureTime);

}

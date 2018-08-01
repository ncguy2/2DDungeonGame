package net.ncguy.lib.foundation.collections;

import java.util.ArrayList;

public class HistoryQueue<T> extends ArrayList<T> {

    private final int maxSize;

    public HistoryQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        boolean r = super.add(t);
        if(size() > maxSize)
            removeRange(0, size() - maxSize - 1);
        return r;
    }

    public T GetMedian() {
        if(isEmpty())
            return null;
        int idx = Math.round(size() * .5f);
        return get(Math.max(0, Math.min(idx, size() - 1)));
    }

    public T GetYoungest() {
        if(isEmpty())
            return null;
        return get(size() - 1);
    }

    public T GetOldlest() {
        if(isEmpty())
            return null;
        return get(0);
    }

}

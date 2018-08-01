package net.ncguy.lib.foundation.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Curve<T> {

    public final Class<T> type;
    public final List<Item<T>> items;
    public float min;
    public float max;

    public Curve(Class<T> type) {
        this.type = type;
        items = new ArrayList<>();
    }

    public Item<T> Add(T item, float value) {
        Item<T> t = new Item<>(item, value);
        items.add(t);
        items.sort((a, b) -> Float.compare(a.value, b.value));

        min = items.get(0).value;
        max = items.get(items.size() - 1).value;
        return t;
    }

    public T Sample(float alpha) {

        if(alpha <= min)
            return items.get(0).item;

        if(alpha >= max)
            return items.get(items.size() - 1).item;

        Item<T> a = null;
        Item<T> b = null;

        for (Item<T> item : items) {
            if(alpha > item.value) {
                a = item;
                break;
            }
        }

        if(a == null)
            return null;

        int idx = items.indexOf(a) + 1;
        if(items.size() > idx)
            b = items.get(idx);

        if(b == null)
            return a.item;

        return Interp(a, b, alpha);
    }

    public T Interp(Item<T> a, Item<T> b, float alpha) {
        float bNorm = b.value - a.value;
        float alphaNorm = alpha - a.value;

        float normalized = alphaNorm / bNorm;
        return Interp(a.item, b.item, normalized);
    }

    public abstract T Interp(T a, T b, float normalized);

    public static class Item<T> {
        public T item;
        public float value;

        public Item(T item, float value) {
            this.item = item;
            this.value = value;
        }
    }

}

package net.ncguy.lib.foundation.utils;

import javafx.scene.paint.Color;

public class FXColourCurve extends Curve<Color> {

    public FXColourCurve() {
        super(Color.class);
    }

    @Override
    public Color Interp(Color a, Color b, float normalized) {
        return a.interpolate(b, normalized);
    }
}

package net.ncguy.lib.foundation.utils;

import javafx.scene.paint.Color;

public class ColourCurve extends Curve<Color> {

    public ColourCurve() {
        super(Color.class);
    }

    @Override
    public Color Interp(Color a, Color b, float normalized) {
        return a.interpolate(b, normalized);
    }
}

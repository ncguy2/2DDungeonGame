package net.ncguy.util.curve;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.lib.foundation.utils.Curve;

public class GLColourCurve extends Curve<Color> {

    public GLColourCurve() {
        super(Color.class);
    }

    @Override
    public Color Interp(Color a, Color b, float normalized) {
        return a.cpy().lerp(b, normalized);
    }



}

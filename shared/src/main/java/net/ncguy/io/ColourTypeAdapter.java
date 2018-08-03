package net.ncguy.io;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ColourTypeAdapter extends TypeAdapter<Color> {

    @Override
    public void write(JsonWriter out, Color value) throws IOException {
        out.beginArray();

        out.value(value.r);
        out.value(value.g);
        out.value(value.b);
        out.value(value.a);

        out.endArray();
    }

    @Override
    public Color read(JsonReader in) throws IOException {

        Color col = new Color();

        in.beginArray();

        col.r = (float) in.nextDouble();
        col.g = (float) in.nextDouble();
        col.b = (float) in.nextDouble();
        col.a = (float) in.nextDouble();

        in.endArray();

        return col;
    }
}

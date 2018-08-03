package net.ncguy.lib.foundation.io;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class> {

    @Override
    public void write(JsonWriter out, Class value) throws IOException {
        out.value(value.getCanonicalName());
    }

    @Override
    public Class read(JsonReader in) throws IOException {
        String cp = in.nextString();

        try {
            return Class.forName(cp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

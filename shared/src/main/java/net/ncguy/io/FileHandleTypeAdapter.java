package net.ncguy.io;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class FileHandleTypeAdapter extends TypeAdapter<FileHandle> {

    @Override
    public void write(JsonWriter out, FileHandle value) throws IOException {
        out.beginObject();
        out.name("valid").value(value != null);
        if(value != null) {
            out.name("type")
                    .value(value.type()
                            .name());
            out.name("path")
                    .value(value.toString());
        }
        out.endObject();
    }

    @Override
    public FileHandle read(JsonReader in) throws IOException {

        FileHandle handle = null;

        in.beginObject();
        in.nextName(); // valid
        boolean isValid = in.nextBoolean();

        if(isValid) {
            in.nextName(); // type
            String typeStr = in.nextString();
            in.nextName(); // path
            String pathStr = in.nextString();
            Files.FileType type = Files.FileType.valueOf(typeStr);
            handle = Gdx.files.getFileHandle(pathStr, type);
        }

        in.endObject();

        return handle;
    }
}

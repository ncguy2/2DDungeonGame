package net.ncguy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.GsonBuilder;
import net.ncguy.io.ColourTypeAdapter;
import net.ncguy.io.FileHandleTypeAdapter;
import net.ncguy.lib.foundation.io.Json;

public class Initialization {

    public static void Init() {
        Json.WithBuilder(Initialization::Json);
    }

    static void Json(GsonBuilder b) {
        b.registerTypeAdapter(FileHandle.class, new FileHandleTypeAdapter());
        b.registerTypeAdapter(Color.class, new ColourTypeAdapter());
    }

}

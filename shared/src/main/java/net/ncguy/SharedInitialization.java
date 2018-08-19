package net.ncguy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.GsonBuilder;
import net.ncguy.io.ColourTypeAdapter;
import net.ncguy.io.FileHandleTypeAdapter;
import net.ncguy.lib.foundation.startup.Initialisation;
import net.ncguy.lib.foundation.startup.Startup;

@Startup
public class SharedInitialization {

    @Startup(Initialisation.Target.Json)
    public static void Json(GsonBuilder b) {
        b.registerTypeAdapter(FileHandle.class, new FileHandleTypeAdapter());
        b.registerTypeAdapter(Color.class, new ColourTypeAdapter());
    }

}

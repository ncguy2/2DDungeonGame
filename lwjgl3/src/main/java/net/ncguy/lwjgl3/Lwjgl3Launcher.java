package net.ncguy.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import net.ncguy.GameLauncher;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {


//        String script = String.join("\n", Files.readAllLines(new File("assets/scripts/abilities/blink.js").toPath()));
//        ScriptHost.Invoke(null, script, ctx -> {
//            Bindings bindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
//            System.out.println(bindings);
//            ScriptObjectMirror test = (ScriptObjectMirror) bindings.get("test");
//            test.call(bindings);
//        });
        createApplication();
    }

    public static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new GameLauncher(), getDefaultConfiguration());
    }

    public static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("DungeonGame");
        configuration.useOpenGL3(true, 3, 3);
        configuration.setWindowedMode(800, 600);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {

    public static void Init() {
        progressBarShader = new ShaderProgram(Gdx.files.internal("shaders/ui/ui.vert"), Gdx.files.internal("shaders/ui/progressbar/progressBar.frag"));
        System.out.println("progressBarShader");
        System.out.println(progressBarShader.getLog());
    }

    public static void Dispose() {

    }

    public static ShaderProgram progressBarShader;

}

package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.profile.ProfilerHost;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReloadableShader {

    private final String name;
    private final FileHandle vertexShader;
    private final FileHandle fragmentShader;

    private ShaderProgram program;

    public static List<WeakReference<ReloadableShader>> shaders = new ArrayList<>();

    public ReloadableShader(String name, FileHandle vertexShader, FileHandle fragmentShader) {
        ProfilerHost.Start("ReloadableShader::ReloadableShader [" + name + "]");
        Register();
        this.name = name;
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        ReloadImmediate();
        ProfilerHost.End("ReloadableShader::ReloadableShader [" + name + "]");
    }

    private void Register() {
        shaders.add(new WeakReference<>(this));
    }

    public void Reload() {
        Gdx.app.postRunnable(this::ReloadImmediate);
    }

    public void ReloadImmediate() {
        ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
        System.out.println(program.getLog());
        if(program.isCompiled()) {
            if(this.program != null)
                this.program.dispose();
            this.program = program;
            return;
        }
        System.out.println(name + " could not compile");
    }

    public ShaderProgram Program() {
        return program;
    }

    public String getLog() {
        if(program != null)
            return program.getLog();
        return "No program";
    }

    public String Name() {
        return name;
    }
}

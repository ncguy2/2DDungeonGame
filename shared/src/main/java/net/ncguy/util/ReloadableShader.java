package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import net.ncguy.profile.ProfilerHost;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class ReloadableShader<T> {

    protected final String name;
    protected T program;

    public static List<WeakReference<ReloadableShader<?>>> shaders = new ArrayList<>();

    public ReloadableShader(String name) {
        this(name, true);
    }
    public ReloadableShader(String name, boolean register) {
        ProfilerHost.Start("ReloadableShader::ReloadableShader [" + name + "]");
        if(register)
            Register();
        this.name = name;
        ProfilerHost.End("ReloadableShader::ReloadableShader [" + name + "]");
    }

    public abstract T Create();

    private void Register() {
        shaders.add(new WeakReference<>(this));
    }

    public void Reload() {
        Gdx.app.postRunnable(this::ReloadImmediate);
    }

    public abstract void ReloadImmediate();

    public T Program() {
        return program;
    }

    public abstract String getLog();

    public String Name() {
        return name;
    }

    public abstract void Shutdown();
}

package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.entity.component.MaterialSpriteComponent;
import net.ncguy.material.ColourAttribute;
import net.ncguy.material.Material;
import net.ncguy.material.MaterialResolver;
import net.ncguy.material.MaterialResolverWithDefault;
import net.ncguy.particles.AbstractParticleSystem;
import net.ncguy.profile.*;
import net.ncguy.tween.TweenCore;
import net.ncguy.util.DeferredCalls;
import net.ncguy.util.Shaders;
import net.ncguy.world.ThreadedEngine;

import java.lang.ref.Reference;
import java.util.Objects;

import static net.ncguy.script.ScriptUtils.tempPrimitives;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameLauncher extends Game {


    @Override
    public void create() {

        ProfilerHost.PROFILER_ENABLED = CPUProfiler.PROFILING_ENABLED = GPUProfiler.PROFILING_ENABLED = false;

        ProfilerHost.StartFrame();
        ProfilerHost.Start("Loading");

        Material defMtl = new Material("Default");

        MaterialResolver resolver = new MaterialResolverWithDefault(defMtl);
        MaterialSpriteComponent.resolver = resolver;
        Material mtl = new Material("mtl_01");
        mtl.Add(new ColourAttribute(ColourAttribute.ColourType.Diffuse, Color.RED));
        resolver.Register(mtl);

        ProfilerHost.Start("Abilities");
        String xml = Gdx.files.internal("metadata/abilities/AbilitySet1.xml")
                .readString();
        AbilityRegistry.instance().Load(xml);
        ProfilerHost.End("Abilities");
        ProfilerHost.End("Loading");

        ProfilerHost.Start("Initialization");
        ProfilerHost.Start("Tween core");
        TweenCore.instance();
        ProfilerHost.End("Tween core");
        ProfilerHost.Start("VisUI");
        VisUI.load();
        ProfilerHost.End("VisUI");
        ProfilerHost.Start("Deferred calls");
        DeferredCalls.Instance();
        ProfilerHost.End("Deferred calls");
        ProfilerHost.Start("Shaders");
        Shaders.Init();
        ProfilerHost.End("Shaders");

        ProfilerHost.Start("Box2D");
        Box2D.init();
        ProfilerHost.End("Box2D");

        ProfilerHost.End("Initialization");


        ProfilerHost.Start("Screen");
        setScreen(new TestScreen2());
//        setScreen(new MPTestScreen());
        ProfilerHost.End("Screen");

        ProfilerHost.EndFrame();
    }

    @Override
    public void render() {

        ProfilerHost.StartFrame();
        ProfilerHost.Start("Frame preamble");
        float delta = Gdx.graphics.getDeltaTime();
        AbstractParticleSystem.GlobalLife += delta;
        ProfilerHost.Start("Tween manager");
        TweenCore.instance().tweenManager.update(delta);
        ProfilerHost.End();
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());
        ProfilerHost.Start("Deferred calls");
        DeferredCalls.Instance().Update(delta);
        ProfilerHost.End();
        ProfilerHost.End();

        ProfilerHost.Start("Screen render");
        super.render();
        ProfilerHost.End();

        ProfilerHost.Start("Temp. primitives update");
        tempPrimitives.forEach(p -> p.duration -= delta);
        tempPrimitives.removeIf(p -> p.duration <= 0);
        ProfilerHost.End();
        ProfilerHost.EndFrame();

        ProfilerHost.Clear();

        GPUTaskProfile tp;
        while((tp = GPUProfiler.GetFrameResults()) != null) {
            ProfilerHost.Post(new TaskStats(tp));
            GPUProfiler.Recycle(tp);
        }

        CPUTaskProfile cp;
        while((cp = CPUProfiler.GetFrameResults()) != null) {
            ProfilerHost.Post(new TaskStats(cp));
            CPUProfiler.Recycle(cp);
        }

        ProfilerHost.instance().NotifyListeners();

    }

    @Override
    public void dispose() {

        ThreadedEngine.registry.stream()
                .filter(Objects::nonNull)
                .map(Reference::get)
                .filter(Objects::nonNull)
                .forEach(ThreadedEngine::Shutdown);

        ThreadedEngine.registry.clear();

        super.dispose();
        Shaders.Dispose();
    }
}
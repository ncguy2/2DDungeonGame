package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.assets.AssetHandler;
import net.ncguy.entity.component.MaterialSpriteComponent;
import net.ncguy.input.ScrollInputHelper;
import net.ncguy.lib.foundation.config.Config;
import net.ncguy.lib.foundation.config.Configuration;
import net.ncguy.lib.foundation.startup.Initialisation;
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
import net.ncguy.world.WindManager;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.function.Supplier;

import static net.ncguy.script.ScriptUtils.tempPrimitives;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameLauncher extends Game {

    long startTime;
    boolean firstFrame = true;
    boolean secondFrame = true;

    @Config(Name = "game.LaunchScreen", Type = LaunchScreen.class)
    public LaunchScreen launchScreen = LaunchScreen.TestScreen2;

    @Override
    public void create() {

        ProfilerHost.SupportsGPUProfiling();

        Configuration.Inject(this);

        System.out.println("First frame time measurement started");
        startTime = System.nanoTime();

        Initialisation.Init();
//        ProfilerHost.PROFILER_ENABLED = CPUProfiler.PROFILING_ENABLED = GPUProfiler.PROFILING_ENABLED = false;

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
        setScreen(launchScreen.Get());
//        setScreen(new TestScreen2());
//        setScreen(new MPTestScreen());
        ProfilerHost.End("Screen");

        ProfilerHost.EndFrame();
    }

    @Override
    public void render() {

        long time = System.nanoTime();
        if(firstFrame) {
            System.out.printf("Time until first frame: %fms\n", (time - startTime) / 1000000f);
            firstFrame = false;
        }else if(secondFrame) {
            System.out.printf("Time until second frame: %fms\n", (time - startTime) / 1000000f);
            secondFrame = false;
        }

        ProfilerHost.StartFrame();
        ProfilerHost.Start("Frame preamble");
        ProfilerHost.Start("AssetHandler::Update");
        AssetHandler.WithInstanceIfExists(AssetHandler::Update);
        ProfilerHost.End("AssetHandler::Update");
        float delta = Gdx.graphics.getDeltaTime();
        AbstractParticleSystem.GlobalLife += delta;
        ProfilerHost.Start("Tween manager");
        TweenCore.instance().tweenManager.update(delta);
        ProfilerHost.End();
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());
        ProfilerHost.Start("Deferred calls");
        DeferredCalls.Instance().Update(delta);
        ProfilerHost.End();

        ProfilerHost.Start("Wind update");
        WindManager.UpdateAll(delta);
        ProfilerHost.End("Wind update");

        ProfilerHost.End();

        ProfilerHost.Start("Screen render");
        super.render();
        ProfilerHost.End();

        ProfilerHost.Start("Temp. primitives update");
        tempPrimitives.forEach(p -> p.duration -= delta);
        tempPrimitives.removeIf(p -> p.duration <= 0);
        ProfilerHost.End();
        ProfilerHost.EndFrame();

        long s = System.nanoTime();

        ProfilerHost.Clear();

        // TODO reimplement properly
//        GPUTaskProfile tp;
//        while((tp = GPUProfiler.GetFrameResults()) != null) {
//            ProfilerHost.Post(new TaskStats(tp));
//            GPUProfiler.Recycle(tp);
//        }
//
//        CPUTaskProfile cp;
//        while((cp = CPUProfiler.GetFrameResults()) != null) {
//            ProfilerHost.Post(new TaskStats(cp));
//            CPUProfiler.Recycle(cp);
//        }

        ProfilerHost.instance().NotifyListeners();
        ScrollInputHelper.instance().Update(delta);
        long e = System.nanoTime();
//        System.out.printf("Profiler overhead for frame %d: %fms\n", Gdx.graphics.getFrameId(), (e - s) / 1000000f);

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

    public enum LaunchScreen {
        ParticleTest(ParticleTestScreen::new),
        TestScreen(TestScreen::new),
        TestScreen2(TestScreen2::new),
        MPTestScreen(MPTestScreen::new),
        ;

        private final Supplier<Screen> func;
        LaunchScreen(Supplier<Screen> func) {
            this.func = func;
        }

        public Screen Get() {
            return func.get();
        }
    }

}
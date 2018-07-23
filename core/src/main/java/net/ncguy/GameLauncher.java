package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.ability.AbilityRegistry;
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
        TweenCore.instance();
        String xml = Gdx.files.internal("metadata/abilities/AbilitySet1.xml")
                .readString();
        AbilityRegistry.instance().Load(xml);
        VisUI.load();

        DeferredCalls.Instance();

        Shaders.Init();

        Box2D.init();
        setScreen(new TestScreen2());
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        TweenCore.instance().tweenManager.update(delta);
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());
        DeferredCalls.Instance().Update(delta);

        super.render();

        tempPrimitives.forEach(p -> p.duration -= delta);
        tempPrimitives.removeIf(p -> p.duration <= 0);
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
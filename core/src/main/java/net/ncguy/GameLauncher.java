package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import net.ncguy.ability.AbilityRegistry;

import static net.ncguy.script.ScriptUtils.tempPrimitives;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameLauncher extends Game {


    @Override
    public void create() {
        String xml = Gdx.files.internal("metadata/abilities/AbilitySet1.xml")
                .readString();
        AbilityRegistry.instance().Load(xml);

        Box2D.init();
        setScreen(new TestScreen());
    }

    @Override
    public void render() {
        super.render();

        float delta = Gdx.graphics.getDeltaTime();

        tempPrimitives.forEach(p -> p.duration -= delta);
        tempPrimitives.removeIf(p -> p.duration <= 0);
    }

}
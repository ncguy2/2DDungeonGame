package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Box2D;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameLauncher extends Game {
    @Override
    public void create() {

//        AbilityRegistry.instance().Load(Gdx.files.internal("metadata/abilities/AbilitySet1.xml").readString());

        Box2D.init();
        setScreen(new TestScreen());

    }
}
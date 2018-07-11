package net.ncguy.physics.data;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BodyFixturesDef {

    public Body body;
    public FixtureDef[] definitions;

    public BodyFixturesDef(Body body, FixtureDef[] definitions) {
        this.body = body;
        this.definitions = definitions;
    }
}

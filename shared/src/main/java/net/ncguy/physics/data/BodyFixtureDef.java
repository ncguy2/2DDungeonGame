package net.ncguy.physics.data;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BodyFixtureDef {

    public Body body;
    public FixtureDef definition;

    public BodyFixtureDef(Body body, FixtureDef definition) {
        this.body = body;
        this.definition = definition;
    }
}

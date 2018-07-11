package net.ncguy.physics.data;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class BodyFixture {

    public Body body;
    public Fixture fixture;

    public BodyFixture(Body body, Fixture fixture) {
        this.body = body;
        this.fixture = fixture;
    }
}

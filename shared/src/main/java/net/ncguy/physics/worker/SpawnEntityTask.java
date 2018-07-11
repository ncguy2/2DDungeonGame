package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class SpawnEntityTask extends PhysicsTask<Body> {

    BodyDef bodyDef;
    FixtureDef fixtureDef;

    public SpawnEntityTask(BodyDef bodyDef, FixtureDef fixtureDef) {
        this.bodyDef = bodyDef;
        this.fixtureDef = fixtureDef;
    }

    @Override
    public Body Run() {

        CreateBodyTask bodyTask = new CreateBodyTask(bodyDef);
        bodyTask.TakeMeta(this);
        Body body = bodyTask.Run();

        CreateFixtureTask fixtureTask = new CreateFixtureTask(body, fixtureDef);
        fixtureTask.TakeMeta(this);
        Fixture fixture = fixtureTask.Run();

        return body;
    }
}

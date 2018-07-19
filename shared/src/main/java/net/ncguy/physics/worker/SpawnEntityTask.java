package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class SpawnEntityTask extends PhysicsTask<Body> {

    BodyDef bodyDef;
    FixtureDef[] fixtureDefs;

    public SpawnEntityTask(BodyDef bodyDef, FixtureDef... fixtureDefs) {
        this.bodyDef = bodyDef;
        this.fixtureDefs = fixtureDefs;
    }

    @Override
    public Body Run() {

        CreateBodyTask bodyTask = new CreateBodyTask(bodyDef);
        bodyTask.TakeMeta(this);
        Body body = bodyTask.Run();

        for (FixtureDef fixtureDef : fixtureDefs) {
            CreateFixtureTask fixtureTask = new CreateFixtureTask(body, fixtureDef);
            fixtureTask.TakeMeta(this);
            fixtureTask.Run();
        }

        return body;
    }
}

package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class CreateFixtureTask extends PhysicsTask<Fixture> {

    protected final Body body;
    protected final FixtureDef def;

    public CreateFixtureTask(Body body, FixtureDef def) {
        this.body = body;
        this.def = def;
    }

    @Override
    public Fixture Run() {
        int i = service.QueueCreateFixture(body, def);
        Fixture fixture;
        while((fixture = service.ObtainFixture(i)) == null) Sleep(1);
        return fixture;
    }

}

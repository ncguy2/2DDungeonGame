package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class CreateBodyTask extends PhysicsTask<Body> {

    protected final BodyDef def;

    public CreateBodyTask(BodyDef def) {
        this.def = def;
    }

    @Override
    public Body Run() {
        int i = service.QueueCreateBody(def);
        Body body;
        while((body = service.ObtainBody(i)) == null) Sleep(1);
        return body;
    }

}

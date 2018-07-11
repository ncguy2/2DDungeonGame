package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.Body;

public class DestroyBodyTask extends PhysicsTask.VoidPhysicsTask {

    protected final Body body;

    public DestroyBodyTask(Body body) {
        this.body = body;
    }

    @Override
    public void Task() {
        service.QueueRemoveBody(body);
    }

}

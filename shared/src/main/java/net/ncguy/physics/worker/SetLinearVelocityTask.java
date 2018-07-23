package net.ncguy.physics.worker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SetLinearVelocityTask extends PhysicsTask.VoidPhysicsTask {

    public final Body body;
    public final Vector2 velocity;

    public SetLinearVelocityTask(Body body, Vector2 velocity) {
        this.body = body;
        this.velocity = velocity;
    }

    @Override
    public void Task() {
        service.QueueTask(() -> {
            if (body.isActive() && body.getWorld() != null)
                this.body.setLinearVelocity(velocity);
        });
    }

}

package net.ncguy.physics.worker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SetTransformTask extends PhysicsTask.VoidPhysicsTask {

    protected final Body body;
    protected final Vector2 translation;
    protected final float rotation;

    public SetTransformTask(Body body, Vector2 translation, float rotation) {
        this.body = body;
        this.translation = translation;
        this.rotation = rotation;
    }

    @Override
    public void Task() {
        service.QueueTask(() -> {
            if (body.isActive() && body.getWorld() != null)
                this.body.setTransform(this.translation, this.rotation);
        });
    }

}

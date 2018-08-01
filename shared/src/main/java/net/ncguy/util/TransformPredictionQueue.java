package net.ncguy.util;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.entity.Transform2D;
import net.ncguy.lib.foundation.collections.PredictionQueue;

public class TransformPredictionQueue extends PredictionQueue<Transform2D> {

    public TransformPredictionQueue(int maxSize) {
        super(maxSize);
    }

    @Override
    public Transform2D Predict(float futureTime) {
        Transform2D c = GetYoungest();

        if(futureTime <= 0)
            return c;

        Transform2D a = GetOldlest();
        Transform2D b = GetMedian();

        if(a == null)
            return null;

        if(b == null || b == c)
            return a;

        if(c == null || a == b)
            return b;

        Transform2D target = new Transform2D();
        // Translation
        Vector2 velocity = new Vector2();
        // A -> B
        velocity.set(b.translation.cpy().sub(a.translation));
        // B -> C
        velocity.set(c.translation.cpy().sub(b.translation));
        float speed = velocity.len() * futureTime;
        velocity.nor().scl(speed);
        target.translation.set(c.translation).add(velocity);

        // Rotation
        float rotation = 0;
        // A -> B
        rotation += b.rotationDegrees - a.rotationDegrees;
        // B -> C
        rotation += c.rotationDegrees - b.rotationDegrees;
        target.rotationDegrees += rotation * futureTime;

        // Scale
        Vector2 scale = new Vector2();
        // A -> B
        scale.set(b.scale.cpy().sub(a.scale));
        // B -> C
        scale.set(c.scale.cpy().sub(b.scale));
        float scaleSpeed = scale.len() * futureTime;
        scale.nor().scl(scaleSpeed);
        target.scale.set(c.scale).add(scale);

        return target;
    }
}

package net.ncguy.tween.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;

public class BodyTweenAccessor implements TweenAccessor<Body> {

    public static final int TRA_X = 2;
    public static final int TRA_Y = 4;
    public static final int ROT = 8;

    public static final int Translation = TRA_X | TRA_Y;

    // TODO add support for world coordinates

    public boolean is(int value, int mask) {
        return (value & mask) == mask;
    }

    @Override
    public int getValues(Body target, int tweenType, float[] returnValues) {

        Transform transform = target.getTransform();


        int amt = 0;
        if(is(tweenType, TRA_X)) returnValues[amt++] = transform.getPosition().x;
        if(is(tweenType, TRA_Y)) returnValues[amt++] = transform.getPosition().y;
        if(is(tweenType, ROT)) returnValues[amt++] = (float) Math.toDegrees(transform.getRotation());
        return amt;
    }

    @Override
    public void setValues(Body target, int tweenType, float[] newValues) {
        Vector2 translation = target.getPosition();
        float rotationRad = target.getAngle();
        int amt = 0;
        if(is(tweenType, TRA_X)) translation.x = newValues[amt++];
        if(is(tweenType, TRA_Y)) translation.y = newValues[amt++];
        if(is(tweenType, ROT)) rotationRad = (float) Math.toRadians(newValues[amt++]);
        target.setTransform(translation, rotationRad);
    }
}

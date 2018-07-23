package net.ncguy.tween.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import net.ncguy.entity.Transform2D;

public class TransformTweenAccessor implements TweenAccessor<Transform2D> {

    public static final int LOCAL = 0;
    public static final int WORLD = 1;
    public static final int TRA_X = 2;
    public static final int TRA_Y = 4;
    public static final int ROT = 8;
    public static final int SCL_X = 16;
    public static final int SCL_Y = 32;

    public static final int Translation = TRA_X | TRA_Y;

    // TODO add support for world coordinates

    public boolean is(int value, int mask) {
        return (value & mask) == mask;
    }

    @Override
    public int getValues(Transform2D target, int tweenType, float[] returnValues) {
        int amt = 0;
        if(is(tweenType, TRA_X)) returnValues[amt++] = target.translation.x;
        if(is(tweenType, TRA_Y)) returnValues[amt++] = target.translation.y;
        if(is(tweenType, ROT)) returnValues[amt++] = target.rotationDegrees;
        if(is(tweenType, SCL_X)) returnValues[amt++] = target.scale.x;
        if(is(tweenType, SCL_Y)) returnValues[amt++] = target.scale.y;
        return amt;
    }

    @Override
    public void setValues(Transform2D target, int tweenType, float[] newValues) {
        int amt = 0;
        if(is(tweenType, TRA_X)) target.translation.x = newValues[amt++];
        if(is(tweenType, TRA_Y)) target.translation.y = newValues[amt++];
        if(is(tweenType, ROT)) target.rotationDegrees = newValues[amt++];
        if(is(tweenType, SCL_X)) target.scale.x = newValues[amt++];
        if(is(tweenType, SCL_Y)) target.scale.y = newValues[amt++];
    }
}

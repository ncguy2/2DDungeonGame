package net.ncguy.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.physics.box2d.Body;
import net.ncguy.entity.Transform2D;
import net.ncguy.tween.accessors.BodyTweenAccessor;
import net.ncguy.tween.accessors.TransformTweenAccessor;

public class TweenCore {

    private static TweenCore instance;
    public static TweenCore instance() {
        if (instance == null)
            instance = new TweenCore();
        return instance;
    }

    public final TweenManager tweenManager;

    private TweenCore() {
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Transform2D.class, new TransformTweenAccessor());
        Tween.registerAccessor(Body.class, new BodyTweenAccessor());
        tweenManager = new TweenManager();
    }

}

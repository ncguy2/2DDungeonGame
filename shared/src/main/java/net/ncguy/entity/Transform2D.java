package net.ncguy.entity;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class Transform2D {

    public Transform2D parent;

    /**
     * Whether this transform is likely to change frequently<br>
     * Recommended to enable this on "living" entities, and disable on environmental entities
     */
    public boolean bIsVolatile = true;

//    protected float translationX = 0;
//    protected float translationY = 0;
//
//    protected float rotationDegrees = 0;
//
//    protected float scaleX = 1;
//    protected float scaleY = 1;

    public final Vector2 translation = new Vector2();
    public float rotationDegrees = 0;
    public final Vector2 scale = new Vector2(1, 1);

    public final Matrix3 transformation = new Matrix3();

    public Matrix3 Update() {
        transformation.idt();
        transformation.translate(translation);
        transformation.rotate(rotationDegrees);
        transformation.scale(scale);
        return transformation;
    }

    public Matrix3 LocalTransform() {
        if(bIsVolatile)
            return Update();
        return transformation;
    }

    public Matrix3 WorldTransform() {
        Matrix3 mat = new Matrix3().idt();
        if(parent != null)
            mat.set(parent.WorldTransform());
        return mat.mul(this.LocalTransform());
    }

    public float RotationRad() {
        return (float) Math.toRadians(rotationDegrees);
    }

    public void Translate(Vector2 vec) {
        translation.add(vec);
        Update();
    }

    protected final Vector2 worldTranslation = new Vector2();
    protected final Vector2 worldScale = new Vector2(1, 1);

    public Vector2 WorldTranslation() {
        WorldTransform().getTranslation(worldTranslation);
        return worldTranslation;
    }
    public float WorldRotation() {
        return WorldTransform().getRotation();
    }
    public Vector2 WorldScale() {
        WorldTransform().getScale(worldScale);
        return worldScale;
    }

    public float WorldRotationRad() {
        return (float) Math.toRadians(WorldRotation());
    }

    public void LerpLocalToWorld(Transform2D target, float alpha) {
        translation.lerp(target.WorldTranslation(), alpha);
        rotationDegrees = rotationDegrees + (target.WorldRotation() - rotationDegrees) * alpha;
        scale.lerp(target.WorldScale(), alpha);
    }

    public void LerpLocalToLocal(Transform2D target, float alpha) {
        translation.lerp(target.translation, alpha);
        rotationDegrees = rotationDegrees + (target.rotationDegrees - rotationDegrees) * alpha;
        scale.lerp(target.scale, alpha);
    }

    public void Set(Transform2D newTransform) {
        translation.set(newTransform.translation);
        rotationDegrees = newTransform.rotationDegrees;
        scale.set(newTransform.scale);
    }
}

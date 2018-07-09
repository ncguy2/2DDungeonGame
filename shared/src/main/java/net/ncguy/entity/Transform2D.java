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

    protected final Vector2 translation = new Vector2();
    protected float rotationDegrees = 0;
    protected final Vector2 scale = new Vector2(1, 1);

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

}

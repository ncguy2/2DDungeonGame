package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraComponent extends SceneComponent {

    public transient Camera camera;

    public CameraComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {

        Matrix3 matrix3 = transform.WorldTransform();
        Vector2 translation = matrix3.getTranslation(new Vector2());

        camera.position.lerp(new Vector3(translation, 0.f), delta * 5);
//        camera.position.set(translation, 0.f);
        camera.update();

        super.Update(delta);
    }

    @Override
    public boolean CanReplicate() {
        return false;
    }
}

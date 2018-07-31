package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Camera;

public class CameraComponent extends SceneComponent {

    public transient Camera camera;

    public CameraComponent() {
        this("Unnamed Scene component");
    }

    public CameraComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {

//        camera.position.lerp(new Vector3(translation, 0.f), delta * 5);
        camera.position.set(transform.WorldTranslation(), 0.f);
        camera.update();

        super.Update(delta);
    }

    @Override
    public boolean CanReplicate() {
        return false;
    }
}

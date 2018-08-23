package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * @see net.ncguy.entity.component.ui.RenderingCameraComponent
 */
@Deprecated
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

        // Input
        if(camera instanceof OrthographicCamera) {
            InputComponent input = GetOwningEntity().GetComponent(InputComponent.class, true);
            float resolve = input.zoomAxis.Resolve() * (delta * 10);
            if(resolve != 0.f) {
                OrthographicCamera oCam = (OrthographicCamera) camera;
                oCam.zoom += resolve;
                oCam.zoom = Math.max(1f, Math.min(oCam.zoom, 2.5f));
                System.out.println("resolve: " + resolve + ", new zoom: " + oCam.zoom);
            }
        }

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

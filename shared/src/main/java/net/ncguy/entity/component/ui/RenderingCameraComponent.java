package net.ncguy.entity.component.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.entity.component.IPropertyProvider;
import net.ncguy.entity.component.InputComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.render.PostProcessingCamera;

import java.util.Collection;

public class RenderingCameraComponent extends SceneComponent implements IPropertyProvider {

    public transient PostProcessingCamera camera;

    public RenderingCameraComponent() {
        this("Unnamed Camera component");
    }

    public RenderingCameraComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {

        Camera camera = this.camera.camera;

        if(camera != null) {

            // Input
            if (camera instanceof OrthographicCamera) {
                InputComponent input = GetOwningEntity().GetComponent(InputComponent.class, true);
                float resolve = input.zoomAxis.Resolve() * (delta * 10);
                if (resolve != 0.f) {
                    OrthographicCamera oCam = (OrthographicCamera) camera;
                    oCam.zoom += resolve;
                    oCam.zoom = Math.max(1f, Math.min(oCam.zoom, 2.5f));
                    System.out.println("resolve: " + resolve + ", new zoom: " + oCam.zoom);
                }
            }

            camera.position.set(transform.WorldTranslation(), 0.f);
            camera.update();
        }

        super.Update(delta);
    }

    @Override
    public boolean CanReplicate() {
        return false;
    }

    @Override
    public void Provide(Collection<FieldPropertyDescriptorLite> descriptors) {
        camera.Provide(descriptors);
    }
}

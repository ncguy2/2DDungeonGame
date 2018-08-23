package net.ncguy.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import net.ncguy.entity.component.EntityProperty;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.entity.component.IPropertyProvider;
import net.ncguy.post.MultiPostProcessor;
import net.ncguy.viewport.FBO;
import net.ncguy.viewport.FBOBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PostProcessingCamera implements IPropertyProvider {

    public final Camera camera;

    @EntityProperty(Type = List.class, Name = "Post processors", Description = "The post processors used by this camera", Category = "Camera", ElementType = MultiPostProcessor.class)
    public final List<MultiPostProcessor> processors;

    public FBO fbo;
    public FBO flattenFBO;
    public int textureSlidesStart = 4;

    public Texture[] postProcessedTextures;

    public PostProcessingCamera(Camera camera, MultiPostProcessor... processors) {
        this.camera = camera;
        this.processors = Arrays.asList(processors);
        this.fbo = FBOBuilder.BuildDefaultGBuffer(Math.round(camera.viewportWidth), Math.round(camera.viewportHeight));
        flattenFBO = new FBO(Pixmap.Format.RGBA8888, Math.round(camera.viewportWidth), Math.round(camera.viewportHeight), false);
    }

    public Camera Camera() {
        return camera;
    }

    public void Resize(int width, int height) {
        if(fbo != null)
            fbo.Resize(width, height);

        if(flattenFBO != null)
            flattenFBO.Resize(width, height);

        forEach(p -> p.Resize(width, height));
    }

    public Texture[] Process(SpriteBatch batch, float delta) {
        postProcessedTextures = fbo.GetTextures().orElse(new Texture[0]);

        if(batch.isDrawing()) {
            System.out.println("Batch shouldn't be drawing here");
            batch.end();
        }

        forEach(p -> postProcessedTextures = p.Render(batch, camera, postProcessedTextures, delta));
        return postProcessedTextures;
    }

    public Texture Flatten(SpriteBatch batch, Texture... textures) {
        flattenFBO.begin();
        flattenFBO.clear(0, 0, 0, 1, false);
        batch.setShader(null);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, flattenFBO.getWidth(), flattenFBO.getHeight()));
        batch.begin();
        for (int i = textureSlidesStart; i < textures.length; i++) {
            batch.draw(textures[i], 0, 0, flattenFBO.getWidth(), flattenFBO.getHeight());
        }
        batch.end();
        flattenFBO.end();

        return flattenFBO.getColorBufferTexture();
    }

    public Texture ProcessAndFlatten(SpriteBatch batch, float delta) {
        return Flatten(batch, Process(batch, delta));
    }

    // Camera accessors
    public Matrix4 Projection() {
        return camera.projection;
    }
    public Matrix4 View() {
        return camera.view;
    }
    public Matrix4 InverseProjectionView() {
        return camera.invProjectionView;
    }
    public Matrix4 Combined() {
        return camera.combined;
    }

    // Processors delegate
    public boolean addAll(Collection<? extends MultiPostProcessor> c) {
        return processors.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends MultiPostProcessor> c) {
        return processors.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return processors.removeAll(c);
    }

    public MultiPostProcessor get(int index) {
        return processors.get(index);
    }

    public MultiPostProcessor set(int index, MultiPostProcessor element) {
        return processors.set(index, element);
    }

    public void add(int index, MultiPostProcessor element) {
        processors.add(index, element);
    }

    public MultiPostProcessor remove(int index) {
        return processors.remove(index);
    }

    public void forEach(Consumer<? super MultiPostProcessor> action) {
        processors.forEach(action);
    }

    // Camera delegate
    public void update() {
        camera.update();
    }

    public void update(boolean updateFrustum) {
        camera.update(updateFrustum);
    }

    public void lookAt(float x, float y, float z) {
        camera.lookAt(x, y, z);
    }

    public void lookAt(Vector3 target) {
        camera.lookAt(target);
    }

    public void normalizeUp() {
        camera.normalizeUp();
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        camera.rotate(angle, axisX, axisY, axisZ);
    }

    public void rotate(Vector3 axis, float angle) {
        camera.rotate(axis, angle);
    }

    public void rotate(Matrix4 transform) {
        camera.rotate(transform);
    }

    public void rotate(Quaternion quat) {
        camera.rotate(quat);
    }

    public void rotateAround(Vector3 point, Vector3 axis, float angle) {
        camera.rotateAround(point, axis, angle);
    }

    public void transform(Matrix4 transform) {
        camera.transform(transform);
    }

    public void translate(float x, float y, float z) {
        camera.translate(x, y, z);
    }

    public void translate(Vector3 vec) {
        camera.translate(vec);
    }

    public Vector3 unproject(Vector3 screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.unproject(screenCoords, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Vector3 unproject(Vector3 screenCoords) {
        return camera.unproject(screenCoords);
    }

    public Vector3 project(Vector3 worldCoords) {
        return camera.project(worldCoords);
    }

    public Vector3 project(Vector3 worldCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.project(worldCoords, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Ray getPickRay(float screenX, float screenY, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.getPickRay(screenX, screenY, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Ray getPickRay(float screenX, float screenY) {
        return camera.getPickRay(screenX, screenY);
    }

    @Override
    public void Provide(Collection<FieldPropertyDescriptorLite> descriptors) {
        FieldPropertyDescriptorLite.OfClass(this, descriptors);
    }
}

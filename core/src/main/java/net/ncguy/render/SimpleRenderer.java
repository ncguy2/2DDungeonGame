package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.assets.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.MaterialSpriteComponent;
import net.ncguy.entity.component.PrimitiveCircleComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.viewport.FBOBuilder;
import net.ncguy.world.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleRenderer extends BaseRenderer {

    FBO gBuffer;
    ReloadableShaderProgram gBufferShader;

    public SimpleRenderer(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);
        ProfilerHost.Start("SimpleRenderer::SimpleRenderer");

        ProfilerHost.Start("Buffer setup");
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        gBuffer = FBOBuilder.BuildDefaultGBuffer(width, height);
        ProfilerHost.End("Buffer setup");

        ProfilerHost.Start("Texture setup");
        ProfilerHost.End("Texture setup");

        ProfilerHost.Start("Shader setup");
        gBufferShader = new ReloadableShaderProgram("SimpleRenderer::GBuffer", Gdx.files.internal("shaders/world.vert"), Gdx.files.internal("shaders/gbuffer.frag"));
        ProfilerHost.End("Shader setup");
        ProfilerHost.End("SimpleRenderer::SimpleRenderer");
    }

    @Override
    public void Resize(int width, int height) {
        super.Resize(width, height);
        gBuffer.Resize(width, height);
    }

    @Override
    public Texture GetTexture() {
        return gBuffer.getTextureAttachments().get(0);
    }

    @Override
    public void Render(float delta) {
        ProfilerHost.Start("SimpleRenderer::Render");
        ProfilerHost.Start("GBuffer");
        // GBuffer
        gBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.setShader(gBufferShader.Program());
        batch.begin();

        //noinspection unchecked
        ProfilerHost.Start("Entity render");
        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(RenderComponent.class);

        ProfilerHost.Start("Fetch");
        List<RenderComponent> components = new ArrayList<>();
        entities.stream().map(e -> e.GetComponents(RenderComponent.class, true)).forEach(components::addAll);
        ProfilerHost.End("Fetch");

        ProfilerHost.Start("Grouping");
        Map<? extends Class<?>, List<RenderComponent>> collect = components.stream()
                .collect(Collectors.groupingBy(Object::getClass));
        List<RenderComponent> materialRenderers = new ArrayList<>();
        List<RenderComponent> otherRenderers = new ArrayList<>();

        collect.entrySet().stream().filter(e -> e.getKey().equals(MaterialSpriteComponent.class)).map(Map.Entry::getValue).forEach(materialRenderers::addAll);
        collect.entrySet().stream().filter(e -> !e.getKey().equals(MaterialSpriteComponent.class)).map(Map.Entry::getValue).forEach(otherRenderers::addAll);

        Map<String, List<MaterialSpriteComponent>> groupedMtls = materialRenderers.stream()
                .map(c -> (MaterialSpriteComponent) c)
                .collect(Collectors.groupingBy(MaterialSpriteComponent::MaterialRef));

        ProfilerHost.End("Grouping");

        if(!groupedMtls.isEmpty()) {
            ProfilerHost.Start("Material rendering [" + groupedMtls.size() + "]");
            groupedMtls.forEach((mtlId, comps) -> {
                ProfilerHost.Start(mtlId + " rendering [" + comps.size() + "]");
                comps.get(0)
                        .Resolve(batch);
                comps.forEach(this::Accept);
                batch.flush();
                ProfilerHost.End("Rendering");
            });
            ProfilerHost.End("Material rendering");
        }

        if(!otherRenderers.isEmpty()) {
            ProfilerHost.Start("Deprecated rendering");
            otherRenderers.forEach(this::Accept);
            ProfilerHost.End("Deprecated rendering");
        }

//        entities.forEach(this::Accept);
        ProfilerHost.End("Entity render");

        Vector2 pos = new Vector2();
        entities = engine.world.GetFlattenedEntitiesWithComponents(PrimitiveCircleComponent.class);
        ProfilerHost.Start("Primitive render [" + entities.size() + "]");
        for (Entity entity : entities) {
            List<PrimitiveCircleComponent> circles = entity.GetComponents(PrimitiveCircleComponent.class, true);
            for (PrimitiveCircleComponent circle : circles) {
                ProfilerHost.Start("SimpleRenderer::Render [" + circle.name + "]");
                circle.transform.WorldTransform().getTranslation(pos);
                Sprites.Ball()
                        .setBounds(pos.x - 32, pos.y - 32, 64, 64);
                Sprites.Ball()
                        .setColor(circle.colour);
                Sprites.Ball()
                        .draw(batch);
                ProfilerHost.End("SimpleRenderer::Render");
            }
        }
        ProfilerHost.End("Primitive render");

        batch.flush();
        gBuffer.end();
        ProfilerHost.End("GBuffer");

        batch.end();
        batch.setShader(null);
        ProfilerHost.End("SimpleRenderer::Render");
    }

    public void Accept(Entity entity) {
        ProfilerHost.Start("SimpleRenderer::Accept [" + entity.Id() + "]");
        List<RenderComponent> renderComponents = entity.GetComponents(RenderComponent.class, true);
        renderComponents.forEach(this::Accept);
        ProfilerHost.End("SimpleRenderer::Accept");
    }

    public void Accept(RenderComponent component) {
        if(component == null) return;
        if(component.GetOwningEntity() == null) return;
        ProfilerHost.Start("SimpleRenderer::Accept " + component.GetOwningEntity().Id() + ": " + component.name);
        component._Render(batch);
        ProfilerHost.End("SimpleRenderer::Accept");
    }

}

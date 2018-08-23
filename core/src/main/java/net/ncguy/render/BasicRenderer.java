package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.MaterialSpriteComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.world.MainEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicRenderer extends AbstractRenderer {

    ReloadableShaderProgram shader;

    public BasicRenderer(MainEngine engine, SpriteBatch batch) {
        super(engine, batch);
        ProfilerHost.Start("BasicRenderer::BasicRenderer");
        shader = new ReloadableShaderProgram("Basic Renderer", Gdx.files.internal("shaders/world.vert"), Gdx.files.internal("shaders/gbuffer.frag"));
        ProfilerHost.End("BasicRenderer::BasicRenderer");
    }

    @Override
    public void Render(PostProcessingCamera camera, float delta) {
        ProfilerHost.Start("BasicRenderer::Render");

        camera.fbo.Begin();
        camera.fbo.clear(0, 0, 0,1, true);

        batch.setProjectionMatrix(camera.Combined());
        batch.setShader(shader.Program());
        batch.begin();

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

        batch.end();
        batch.setShader(null);

        camera.fbo.End();
        ProfilerHost.End("BasicRenderer::Render");
    }

    public void Accept(Entity entity) {
        ProfilerHost.Start("SimpleRenderer::Accept [" + entity.Id() + "]");
        List<RenderComponent> renderComponents = entity.GetComponents(RenderComponent.class, true);
        renderComponents.forEach(this::Accept);
        ProfilerHost.End("SimpleRenderer::Accept");
    }

    public void Accept(RenderComponent component) {
        if(component == null) return;
        ProfilerHost.Start("SimpleRenderer::Accept " + component.name);
        component._Render(batch);
        ProfilerHost.End("SimpleRenderer::Accept");
    }

}

package net.ncguy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.*;
import net.ncguy.entity.component.ui.HealthUIComponent;
import net.ncguy.entity.component.ui.UIComponent;
import net.ncguy.lib.gen.tile.TileWorldElement;
import net.ncguy.lib.gen.tile.TileWorldGenerator;
import net.ncguy.particles.AbstractParticleSystem;
import net.ncguy.particles.BurstParticleSystem;
import net.ncguy.particles.ParticleManager;
import net.ncguy.physics.PhysicsUserObject;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.render.BaseRenderer;
import net.ncguy.render.LightRenderer2;
import net.ncguy.render.ParticleRenderer;
import net.ncguy.script.ScriptUtils;
import net.ncguy.script.SpawnerScriptObject;
import net.ncguy.system.AbilitySystem;
import net.ncguy.system.InputSystem;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.ui.character.CharacterUI;
import net.ncguy.util.DeferredCalls;
import net.ncguy.world.Engine;
import net.ncguy.world.ThreadedEngine;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static net.ncguy.system.PhysicsSystem.screenToPhysics;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class TestScreen2 implements Screen {

    SpriteBatch batch;
    OrthographicCamera camera;

    Texture floorTex;
    Texture wallTex;
    Box2DDebugRenderer debugRenderer;
    ShapeRenderer renderer;

    Engine engine;
    ThreadedEngine omtEngine;
    PhysicsSystem physicsSystem;
    String wallTexPath;
    String floorTexPath;
    CharacterUI characterUI;

    Stage stage;
    Viewport stageViewport;
    OrthographicCamera stageCamera;

    BaseRenderer sceneRenderer;
    ParticleRenderer particleRenderer;
    Entity entity;

    AbstractParticleSystem particles;

    @Override
    public void show() {

        ProfilerHost.Start("TestScreen2");

        // Prepare your screen here.
        ShaderProgram.pedantic = false;
//        World.setVelocityThreshold(10);
//        world = new World(new Vector2(0, 0), true);
        ProfilerHost.Start("Initialization");
        debugRenderer = new Box2DDebugRenderer();
        ProfilerHost.Start("Stage");
        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);
        ProfilerHost.End("Stage");

        ProfilerHost.End("Initialization");

        ProfilerHost.Start("Engines");
        ProfilerHost.Start("Main Engine");
        engine = new Engine();
        engine.AddSystem(new InputSystem(engine.world));
        engine.AddSystem(new AbilitySystem(engine.world));
        ProfilerHost.End("Main engine");
        ProfilerHost.Start("OMT engine");
        omtEngine = new ThreadedEngine();
        omtEngine.AddSystem(physicsSystem = new PhysicsSystem(engine.world));
        ProfilerHost.End("OMT engine");
        ProfilerHost.Start("Script engine");
        ScriptUtils.instance()
                .Engine(engine)
                .PhysicsSystem(physicsSystem)
                .World(physicsSystem.World());
        ProfilerHost.End("Script engine");
        ProfilerHost.End("Engines");

        camera = new OrthographicCamera();
        batch = new SpriteBatch();

//        sceneRenderer = new DeferredRenderer(engine, batch, camera);
        ProfilerHost.Start("Renderer");
        sceneRenderer = new LightRenderer2(engine, batch, camera);
        ProfilerHost.End("Renderer");

        ProfilerHost.Start("Textures");
        floorTex = new Texture(Gdx.files.internal(floorTexPath = "textures/wood.png"));
        wallTex = new Texture(Gdx.files.internal(wallTexPath = "textures/wall.jpg"));
        ProfilerHost.End("Textures");

        ProfilerHost.Start("Map");
        float width = 64;
        float height = 64;

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        ProfilerHost.Start("World generation");
        TileWorldGenerator generator = new TileWorldGenerator();
        Collection<TileWorldElement> elements = generator.GetElements();
        ProfilerHost.End("World generation");

        ProfilerHost.Start("World composition [" + elements.size() + "]");
        for (TileWorldElement element : elements) {

            int x = element.x;
            int y = element.y;
            boolean solid = element.solid;

            Entity mapEntity = new Entity();
            mapEntity.SetRootComponent(new SpriteComponent("Sprite")).spriteRef = (solid ? wallTexPath : floorTexPath);
            ((SpriteComponent) mapEntity.GetRootComponent()).castShadow = solid;
            mapEntity.Transform().translation.set(width * x, height * y);
            mapEntity.Transform().scale.set(width, height);

            if (solid) {
                BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;
                def.position.set((x * width), (y * height))
                        .scl(screenToPhysics);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(halfWidth * screenToPhysics, halfHeight * screenToPhysics);

                FixtureDef fixDef = new FixtureDef();
                fixDef.shape = shape;
                fixDef.density = 0;
                fixDef.friction = 0f;
                fixDef.restitution = 0.0f;

                SpawnEntityTask task = new SpawnEntityTask(def, fixDef);
                task.OnFinish(body -> {
                    mapEntity.AddComponent(new CollisionComponent("Collision")).body = body;
                    shape.dispose();
                });

                physicsSystem.GetContainer("Overworld")
                        .map(w -> w.foreman)
                        .ifPresent(f -> f.Post(task));
            }

            engine.world.Add(mapEntity);
        }
        ProfilerHost.End("World composition");
        ProfilerHost.End("Map");


        ProfilerHost.Start("Entities");
        ProfilerHost.Start("Player");
        Entity playerEntity = new Entity();
        ProfilerHost.Start("Physics");
        CollisionComponent collision = playerEntity.SetRootComponent(new CollisionComponent("Collision"));
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(400, 300)
                .scl(screenToPhysics);
//        player = physicsSystem.World().createBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(32f * screenToPhysics);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0.0f;
//        player.createFixture(fixtureDef);

        ProfilerHost.Start("Dispatch");
        SpawnEntityTask task = new SpawnEntityTask(def, fixtureDef);
        task.OnFinish(body -> {
            ((PhysicsUserObject) body.getUserData()).entity = playerEntity;
            collision.body = body;
            shape.dispose();
        });
        physicsSystem.Foreman().Post(task);
        ProfilerHost.End("Dispatch");
        ProfilerHost.End("Physics");

        ProfilerHost.Start("Components");
        playerEntity.AddComponent(new InputComponent("Input"));
        playerEntity.AddComponent(new MovementComponent("Movement")).resetAfterCheck = true;
        playerEntity.AddComponent(new CameraComponent("Camera")).camera = camera;
        playerEntity.AddComponent(new PrimitiveCircleComponent("Body")).colour.set(Color.CYAN);
        HealthComponent health = new HealthComponent("Health");
        playerEntity.AddComponent(health);
        playerEntity.AddComponent(new HealthUIComponent("UI/Health", health));
        playerEntity.AddComponent(new DistortionComponent("Distortion")).spriteRef = "textures/CloudMask.png";
        ProfilerHost.End("Components");
        ProfilerHost.Start("Abilities");
        AbilitiesComponent abilities = playerEntity.AddComponent(new AbilitiesComponent("Abilities"));
        AbilityRegistry.instance().GiveAll(abilities);
        ProfilerHost.End("Abilities");
        ProfilerHost.End("Player");

        ProfilerHost.Start("UI");
        characterUI = new CharacterUI(playerEntity);
        stage.addActor(characterUI);
        ProfilerHost.End("UI");

        Gdx.input.setInputProcessor(stage);

        engine.world.Add(playerEntity);

        ProfilerHost.Start("Spawner entity");
        ProfilerHost.Start("Creation");

        entity = new Entity();
        LightComponent light = entity.AddComponent(new LightComponent("Light"));
        light.colour.set(1, 1, 0, 1);
        light.radius = 256;
        EntitySpawnerComponent spawner = new EntitySpawnerComponent("Spawner");
        spawner.spawnInterval = 2.5f;
        spawner.spawnAmount = 3;
        spawner.spawnerScript = new SpawnerScriptObject(Gdx.files.internal("scripts/spawner.alpha.js")
                .readString());
        spawner.spawnerScript.Parse();
        entity.AddComponent(spawner);
        entity.Transform().translation.set(400, 250);
        ProfilerHost.End("Creation");
        ProfilerHost.Start("Dispatch");
        engine.world.Add(entity);
        ProfilerHost.End("Dispatch");
        ProfilerHost.End("Spawner entity");
        ProfilerHost.End("Entities");

        omtEngine.start();

        Runnable[] spawnTask = new Runnable[1];

        spawnTask[0] = () -> {
            if(particles != null)
                particles.Finish();
//            particles = new TemporalParticleSystem(10240, 3f);
            particles = new BurstParticleSystem(10240);
            DeferredCalls.Instance().Post(1, spawnTask[0]);
        };

        spawnTask[0].run();

        particleRenderer = new ParticleRenderer(engine, batch, camera);

        ProfilerHost.End("TestScreen2");
    }

    Entity AddEntity(Vector2 pos) {
        return AddEntity(pos.x, pos.y);
    }

    Entity AddEntity(float x, float y) {
        Entity otherEntity = new Entity();
        CollisionComponent otherCollision = otherEntity.SetRootComponent(new CollisionComponent("Collision"));
        otherEntity.AddComponent(new PrimitiveCircleComponent("Circle")).colour.set(1, 0, 0, 1);
        HealthComponent healthComponent = new HealthComponent("Health");
        otherEntity.AddComponent(healthComponent);
        otherEntity.AddComponent(new HealthUIComponent("UI/Health", healthComponent));
        Random random = new Random();
//        LightComponent light = otherEntity.AddComponent(new LightComponent("Light"));
//        light.colour.set(random.nextInt()).a = 1f;
//        light.radius = (1 << (new Random().nextInt(10-6) + 6));

        BodyDef otherDef = new BodyDef();
        otherDef.type = BodyDef.BodyType.DynamicBody;
        otherDef.position.set(x, y)
                .scl(screenToPhysics);
//        player = physicsSystem.World().createBody(def);
        CircleShape othershape = new CircleShape();
        othershape.setRadius(32f * screenToPhysics);
        FixtureDef otherfixtureDef = new FixtureDef();
        otherfixtureDef.shape = othershape;
        otherfixtureDef.density = 0f;
        otherfixtureDef.friction = 0f;
        otherfixtureDef.restitution = 0.0f;
//        player.createFixture(fixtureDef);

        SpawnEntityTask othertask = new SpawnEntityTask(otherDef, otherfixtureDef);
        othertask.OnFinish(body -> {
            ((PhysicsUserObject) body.getUserData()).entity = otherEntity;
            otherCollision.body = body;
//            othershape.dispose();
        });
        physicsSystem.Foreman()
                .Post(othertask);

        engine.world.Add(otherEntity);
        return otherEntity;
    }


    @Override
    public void render(float delta) {

        ParticleManager.instance().Update(delta);

//        if(entity != null)
//            entity.GetComponent(LightComponent.class, true).radius = 1024;

        ProfilerHost.Start("TestScreen2::render");
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ProfilerHost.Start("Engine update");
        engine.Update(delta);
        ProfilerHost.End("Engine update");

        ProfilerHost.Start("World Renderer");
        sceneRenderer.Render(delta);
        ProfilerHost.End("World Renderer");

        ProfilerHost.Start("Particle renderer");
        particleRenderer.Render(delta);
        ProfilerHost.End("Particle renderer");

        ProfilerHost.Start("Screen Renderer");
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();

        ProfilerHost.Start("World quad render");
        Texture tex = sceneRenderer.GetTexture();
        TextureRegion reg = new TextureRegion(tex);
        reg.flip(false, true);
        batch.draw(reg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion partReg = new TextureRegion(particleRenderer.GetTexture());
        partReg.flip(false, true);
        batch.draw(partReg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ProfilerHost.End("World quad render");
        batch.setProjectionMatrix(camera.combined);
        ProfilerHost.Start("World UI Render");
        List<Entity> uiEntities = engine.world.GetFlattenedEntitiesWithComponents(UIComponent.class);
        for (Entity uiEntity : uiEntities) {
            List<UIComponent> uiComponents = uiEntity.GetComponents(UIComponent.class, true);
            uiComponents.forEach(ui -> ui.Render(batch));
        }
        ProfilerHost.End("World UI Render");

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {

            ProfilerHost.Start("Debug render");

            ProfilerHost.Start("Temporary primitives");
            if (!ScriptUtils.tempPrimitives.isEmpty()) {
                if (renderer == null)
                    renderer = new ShapeRenderer();
                renderer.setProjectionMatrix(camera.combined);
                renderer.begin(ShapeRenderer.ShapeType.Line);
                ScriptUtils.tempPrimitives.forEach(p -> p._Render(renderer));
                renderer.end();
            }
            ProfilerHost.End("Temporary primitives");

            ProfilerHost.Start("Box2dDebugRenderer");
            debugRenderer.render(physicsSystem.World(), camera.combined.cpy()
                    .scl(PhysicsSystem.physicsToScreen));
            ProfilerHost.End("Box2dDebugRenderer");
            ProfilerHost.End("Debug render");
        }

        ProfilerHost.End("Screen Renderer");

        ProfilerHost.Start("Stage");
        ProfilerHost.Start("Update");
        characterUI.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.act(delta);
        ProfilerHost.End("Update");
        ProfilerHost.Start("Render");
        stage.draw();
        ProfilerHost.End("Render");
        ProfilerHost.End("Stage");

        ProfilerHost.End("TestScreen2::render");
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        camera.setToOrtho(false, width, height);
        sceneRenderer.Resize(width, height);
        stageViewport.update(width, height, true);
        particleRenderer.Resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
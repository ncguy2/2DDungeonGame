package net.ncguy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.assets.TextureResolver;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.*;
import net.ncguy.entity.component.ui.HealthUIComponent;
import net.ncguy.entity.component.ui.UIComponent;
import net.ncguy.input.InputHelper;
import net.ncguy.network.NetworkContainer;
import net.ncguy.particles.ParticleManager;
import net.ncguy.physics.PhysicsUserObject;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.render.BaseRenderer;
import net.ncguy.render.LightRenderer2;
import net.ncguy.script.ScriptUtils;
import net.ncguy.system.AbilitySystem;
import net.ncguy.system.InputSystem;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.ui.character.CharacterUI;
import net.ncguy.world.MainEngine;
import net.ncguy.world.ThreadedEngine;

import java.util.List;

import static net.ncguy.system.PhysicsSystem.screenToPhysics;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class ParticleTestScreen implements Screen {

    SpriteBatch batch;
    OrthographicCamera camera;

    Box2DDebugRenderer debugRenderer;
    ShapeRenderer renderer;

    MainEngine engine;
    ThreadedEngine omtEngine;
    PhysicsSystem physicsSystem;
    CharacterUI characterUI;

    Stage stage;
    Viewport stageViewport;
    OrthographicCamera stageCamera;

    BaseRenderer sceneRenderer;
    Entity entity;

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
        engine = new MainEngine();
        NetworkContainer.engine = engine;
        NetworkContainer.physics = physicsSystem;
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

//        Entity mapEntity = new Entity();
//        GeneratorComponent generator = mapEntity.AddComponent(new GeneratorComponent("Generator"));
//        generator.worldWidth = 16;
//        generator.worldHeight = 16;
////        generator.container = physicsSystem.GetContainer("Overworld").orElse(null);
//        engine.world.Add(mapEntity);
//        generator.Generate();

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
        physicsSystem.Foreman()
                .Post(task);
        ProfilerHost.End("Dispatch");
        ProfilerHost.End("Physics");

        ProfilerHost.Start("Components");
        playerEntity.AddComponent(new InputComponent("Input"));
        playerEntity.AddComponent(new MovementComponent("Movement")).resetAfterCheck = true;
        CameraComponent camComponent = new CameraComponent("Camera");
        camComponent.camera = this.camera;
        LinearLaggingArmComponent cameraArm = new LinearLaggingArmComponent("Camera arm");
        cameraArm.Add(camComponent);
        playerEntity.AddComponent(cameraArm);
//        playerEntity.AddComponent(new PrimitiveCircleComponent("Body")).colour.set(Color.CYAN);

        MaterialSpriteComponent body = playerEntity.AddComponent(new MaterialSpriteComponent("Body"));
        body.spriteRef = "textures/kenney.nl/particlePack/spark_02.png";
        body.materialRef = "mtl_01";
        body.spriteScaleOverride.set(64, 64);


        HealthComponent health = new HealthComponent("Health");
        playerEntity.AddComponent(health);
        playerEntity.AddComponent(new HealthUIComponent("UI/Health", health));
        playerEntity.AddComponent(new DistortionComponent("Distortion")).spriteRef = "textures/CloudMask.png";
        ProfilerHost.End("Components");
        ProfilerHost.Start("Abilities");
        AbilitiesComponent abilities = playerEntity.AddComponent(new AbilitiesComponent("Abilities"));
        AbilityRegistry.instance()
                .GiveAll(abilities);
        ProfilerHost.End("Abilities");
        ProfilerHost.End("Player");

        ProfilerHost.Start("UI");
        characterUI = new CharacterUI(playerEntity);
        stage.addActor(characterUI);
        ProfilerHost.End("UI");

        InputHelper.AddProcessors(stage);

        engine.world.Add(playerEntity);

        ProfilerHost.Start("Particle entity");
        Entity e = new Entity();
        LightComponent root = e.SetRootComponent(new LightComponent("Root"));
        root.radius = 1024;
        root.colour.set(8775816);

        RotationComponent rotator = root.Add(new RotationComponent("Rotator"));

        final Texture[] tex = new Texture[1];

        TextureResolver.GetTextureAsync("textures/testImg.jpg", t -> tex[0] = t);

        ParticleProfileComponent particles = rotator.Add(new ParticleProfileComponent("Particles"));
        particles.systemName = "Texture Test";
        particles.onInit = (comp, sys) -> {
            sys.AddUniform("u_colourTexture", loc -> {
                if(tex[0] != null) {
                    tex[0].bind(5);
                    Gdx.gl.glUniform1i(loc, 5);
                }
            });
        };


        engine.world.Add(e);
        ProfilerHost.End("Particle entity");

        ProfilerHost.End("Entities");

        omtEngine.start();

        ProfilerHost.End("TestScreen2");
    }

    @Override
    public void render(float delta) {

        ParticleManager.instance()
                .Update(delta);

        ProfilerHost.Start("TestScreen2::render");
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ProfilerHost.Start("Engine update");
        engine.Update(delta);
        ProfilerHost.End("Engine update");

        ProfilerHost.Start("World Renderer");
        sceneRenderer.Render(delta);
        ProfilerHost.End("World Renderer");

        ProfilerHost.Start("Screen Renderer");
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();

        ProfilerHost.Start("World quad render");
        Texture tex = sceneRenderer.GetTexture();
        TextureRegion reg = new TextureRegion(tex);
        reg.flip(false, sceneRenderer.ShouldFlipTexture());
        batch.draw(reg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ProfilerHost.End("World quad render");
        batch.setProjectionMatrix(camera.combined);
        ProfilerHost.Start("World UI Render");
        List<Entity> uiEntities = engine.world.GetFlattenedEntitiesWithComponents(UIComponent.class);
        for (Entity uiEntity : uiEntities) {
            List<UIComponent> uiComponents = uiEntity.GetComponents(UIComponent.class, true);
            uiComponents.forEach(ui -> ui._Render(batch));
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
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            ProfilerHost.Start("Render");
            stage.draw();
            ProfilerHost.End("Render");
        }
        ProfilerHost.End("Stage");

        ProfilerHost.End("TestScreen2::render");
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        camera.setToOrtho(false, width, height);
        sceneRenderer.Resize(width, height);
        stageViewport.update(width, height, true);
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
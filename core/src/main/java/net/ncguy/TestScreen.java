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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.asset.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.*;
import net.ncguy.entity.component.ui.HealthUIComponent;
import net.ncguy.entity.component.ui.UIComponent;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.script.ScriptUtils;
import net.ncguy.system.AbilitySystem;
import net.ncguy.system.InputSystem;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.world.Engine;
import net.ncguy.world.ThreadedEngine;

import java.util.List;

import static net.ncguy.system.PhysicsSystem.physicsToScreen;
import static net.ncguy.system.PhysicsSystem.screenToPhysics;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class TestScreen implements Screen {

    ShaderProgram shader;
    SpriteBatch batch;
    OrthographicCamera camera;
    Texture texture;
    TextureRegion textureRegion;

    boolean[][] solidMap;

    Texture floorTex;
    Texture wallTex;
    Box2DDebugRenderer debugRenderer;
    ShapeRenderer renderer;
    Body player;

    Engine engine;
    ThreadedEngine omtEngine;
    Entity playerEntity;
    PhysicsSystem physicsSystem;

    @Override
    public void show() {
        // Prepare your screen here.
        ShaderProgram.pedantic = false;
//        World.setVelocityThreshold(10);
//        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();


        omtEngine = new ThreadedEngine();


        engine = new Engine();
        engine.AddSystem(new InputSystem(engine.world));
        engine.AddSystem(new AbilitySystem(engine.world));
        omtEngine.AddSystem(physicsSystem = new PhysicsSystem(engine.world));
        ScriptUtils.instance()
                .Engine(engine)
                .PhysicsSystem(physicsSystem)
                .World(physicsSystem.World());

        shader = new ShaderProgram(Gdx.files.internal("shaders/tile/tile.vert"), Gdx.files.internal("shaders/tile/tile.frag"));
        System.out.println(shader.getLog());

        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("textures/connected/debug.png"));

        floorTex = new Texture(Gdx.files.internal("textures/wood.png"));
        wallTex = new Texture(Gdx.files.internal("textures/wall.jpg"));

        textureRegion = new TextureRegion(texture);
        textureRegion.setV2(.4f);

        solidMap = new boolean[12][9];

        for (int i = 0; i < solidMap.length; i++) {
            for (int j = 0; j < solidMap[i].length; j++) {

                if (i == 0 || i == solidMap.length - 1) {
                    solidMap[i][j] = true;
                    continue;
                }
                if (j == 0 || j == solidMap[i].length - 1) {
                    solidMap[i][j] = true;
                    continue;
                }
                solidMap[i][j] = false;
            }
        }

        solidMap[3][3] = true;
        solidMap[3][4] = true;
        solidMap[3][5] = true;

        solidMap[4][3] = true;
        solidMap[4][4] = true;
        solidMap[4][5] = true;

        float width = 64;
        float height = 64;

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        for (int x = 0; x < solidMap.length; x++) {
            for (int y = 0; y < solidMap[x].length; y++) {
                if (solidMap[x][y]) {
                    BodyDef def = new BodyDef();
                    def.type = BodyDef.BodyType.StaticBody;
                    def.position.set((x * width) + halfWidth, (y * height) + halfHeight)
                            .scl(screenToPhysics);

//                    Body body = physicsSystem.World().createBody(def);
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(halfWidth * screenToPhysics, halfHeight * screenToPhysics);

                    FixtureDef fixDef = new FixtureDef();
                    fixDef.shape = shape;
                    fixDef.density = 0;
                    fixDef.friction = 0f;
                    fixDef.restitution = 0.0f;

                    SpawnEntityTask task = new SpawnEntityTask(def, fixDef);
                    task.OnFinish(body -> shape.dispose());
                    physicsSystem.Foreman().Post(task);
                }
            }
        }

        playerEntity = new Entity();
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

        SpawnEntityTask task = new SpawnEntityTask(def, fixtureDef);
        task.OnFinish(body -> {
            collision.body = player = body;
            shape.dispose();
        });
        physicsSystem.Foreman().Post(task);

//        physicsSystem.Foreman()
//                .Post(new PhysicsTask.VoidPhysicsTask() {
//                    @Override
//                    public void Task() {
//                        BodyDef def = new BodyDef();
//                        def.type = BodyDef.BodyType.DynamicBody;
//                        def.position.set(400, 300)
//                                .scl(screenToPhysics);
//
//                        CircleShape shape = new CircleShape();
//                        shape.setRadius(32f * screenToPhysics);
//                        FixtureDef fixtureDef = new FixtureDef();
//                        fixtureDef.shape = shape;
//                        fixtureDef.density = 0f;
//                        fixtureDef.friction = 0f;
//                        fixtureDef.restitution = 0.0f;
//
//                        PhysicsService service = physicsSystem.Service();
//                        service.AddListener(Body.class, l1 -> {
//                            player = service.ObtainBody(l1.id);
//                            service.AddListener(Fixture.class, l2 -> service.ObtainFixture(l2.id)).id = service.QueueCreateFixture(player, fixtureDef);
//                            collision.body = player;
//                        }).id = service.QueueCreateBody(def);
//                    }
//                });

        playerEntity.AddComponent(new InputComponent("Input"));
        playerEntity.AddComponent(new MovementComponent("Movement"));
        playerEntity.AddComponent(new CameraComponent("Camera")).camera = camera;
        HealthComponent health = new HealthComponent("Health");
        playerEntity.AddComponent(health);
        playerEntity.AddComponent(new HealthUIComponent("UI/Health", health));

        AbilityRegistry.instance()
                .Get("Blink")
                .ifPresent(blink -> playerEntity.AddComponent(new AbilityComponent("Ability/Blink"))
                        .SlotIdx(0)
                        .SetAbility(blink));
        AbilityRegistry.instance()
                .Get("Heal")
                .ifPresent(heal -> playerEntity.AddComponent(new AbilityComponent("Ability/Heal"))
                        .SlotIdx(1)
                        .SetAbility(heal));

        AbilityRegistry.instance()
                .Get("Hurt")
                .ifPresent(hurt -> playerEntity.AddComponent(new AbilityComponent("Ability/Hurt"))
                        .SlotIdx(2)
                        .SetAbility(hurt));

        AbilityRegistry.instance()
                .Get("Bladestorm")
                .ifPresent(bladestorm -> playerEntity.AddComponent(new AbilityComponent("Ability/Bladestorm"))
                        .SlotIdx(3)
                        .SetAbility(bladestorm));

        engine.world.Add(playerEntity);
        omtEngine.start();
    }

    public boolean Sample(int x, int y) {
        if (x < 0 || x >= solidMap.length)
            return false;
        if (y < 0 || y >= solidMap[x].length)
            return false;

        return solidMap[x][y];
    }

    public int Sample(int x, int y, int bit) {
        boolean sample = Sample(x - 1, y - 1);
        return sample ? 1 << bit : 0;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.Update(delta);

//        player.setTransform(playerEntity.rootComponent.transform.translation, playerEntity.rootComponent.transform.RotationRad());

        // Draw your screen here. "delta" is the time since last render in seconds.
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(null);
        batch.begin();

        textureRegion.setV2(.2f);

        float width = 64;
        float height = 64;

        for (int x = 0; x < solidMap.length; x++) {
            for (int y = 0; y < solidMap[x].length; y++) {
                Texture t = solidMap[x][y] ? wallTex : floorTex;
                batch.draw(t, width * x, height * y, width, height);
            }
        }

        Vector2 pos = new Vector2();
        if(player != null)
            pos.set(this.player.getPosition()).scl(physicsToScreen);

        Sprites.Ball()
                .setBounds(pos.x - 32, pos.y - 32, 64, 64);
        Sprites.Ball()
                .setColor(Color.CYAN);
        Sprites.Ball()
                .draw(batch);

        List<UIComponent> uiComponents = playerEntity.GetComponents(UIComponent.class, true);
        uiComponents.forEach(ui -> ui.Render(batch));

        batch.end();

        if (!ScriptUtils.tempPrimitives.isEmpty()) {
            if (renderer == null)
                renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            ScriptUtils.tempPrimitives.forEach(p -> p._Render(renderer));
            renderer.end();
        }

        debugRenderer.render(physicsSystem.World(), camera.combined.cpy()
                .scl(PhysicsSystem.physicsToScreen));

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/tile/tile.vert"), Gdx.files.internal("shaders/tile/tile.frag"));
            System.out.println(shader.getLog());
            if (shader.isCompiled()) {
                this.shader.dispose();
                this.shader = shader;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        camera.setToOrtho(false, width, height);
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
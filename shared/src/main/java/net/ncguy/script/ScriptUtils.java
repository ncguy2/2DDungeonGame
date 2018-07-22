package net.ncguy.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;
import net.ncguy.entity.component.CameraComponent;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.entity.component.HealthComponent;
import net.ncguy.physics.worker.DestroyBodyTask;
import net.ncguy.physics.worker.SetTransformTask;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.system.PhysicsContainer;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.world.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class ScriptUtils {

    private static ScriptUtils instance;
    public static ScriptUtils instance() {
        if (instance == null)
            instance = new ScriptUtils();
        return instance;
    }

    private ScriptUtils() {
        entityFactory = new EntityFactory(this);
        ScriptHost.AddGlobalBinding("Utils", this);
    }

    public EntityFactory entityFactory;
    protected Engine engine;
    protected World collisionWorld;
    protected PhysicsSystem physicsSystem;

    public Engine Engine() { return engine; }
    public ScriptUtils Engine(Engine engine) {
        this.engine = engine;
        return this;
    }


    public PhysicsSystem PhysicsSystem() { return physicsSystem; }
    public ScriptUtils PhysicsSystem(PhysicsSystem physicsSystem) {
        this.physicsSystem = physicsSystem;
        return this;
    }

    @Deprecated
    public World World() { return physicsSystem.World(); }
    public Optional<PhysicsContainer> Container(String worldName) { return physicsSystem.GetContainer(worldName); }
    @Deprecated
    public ScriptUtils World(World collisionWorld) {
        this.collisionWorld = collisionWorld;
        return this;
    }
    public float PhysicsToScreen() {
        return PhysicsSystem.physicsToScreen;
    }
    public float ScreenToPhysics() {
        return PhysicsSystem.screenToPhysics;
    }

    public Primitive DebugPoint(Vector2 point, float duration) {
        Point e = new Point(duration);
        e.point.set(point);
        tempPrimitives.add(e);
        return e;
    }
    public Primitive DebugCircle(Vector2 point, float radius, float duration) {
        Circle e = new Circle(duration);
        e.point.set(point);
        e.radius = radius;
        tempPrimitives.add(e);
        return e;
    }
    public Primitive DebugLine(Vector2 start, Vector2 end, float duration) {
        Line e = new Line(duration);
        e.start.set(start);
        e.end.set(end);
        tempPrimitives.add(e);
        return e;
    }
    public Primitive DebugRect(Vector2 pos, Vector2 size, float duration) {
        Rect e = new Rect(duration);
        e.pos.set(pos);
        e.size.set(size);
        tempPrimitives.add(e);
        return e;
    }

    public Vector2 RandomDirection() {
        return new Vector2().setToRandomDirection();
    }
    public float RandomFloat() {
        return new Random().nextFloat();
    }

    public Color RandomColour() {
        Random random = new Random();
        return new Color(random.nextInt());
    }

    public void SetEntityRelativeLocation(Entity entity, float x, float y) {
        Transform2D transform = entity.Transform();
        transform.translation.set(x, y);
        CollisionComponent collision = entity.GetComponent(CollisionComponent.class, false);
//        collision.body.setTransform(x * PhysicsSystem.screenToPhysics, y * PhysicsSystem.screenToPhysics, transform.RotationRad());
        SetTransformTask task = new SetTransformTask(collision.body, new Vector2(x, y).scl(ScreenToPhysics()), transform.RotationRad());
        physicsSystem.GetContainer("Overworld").ifPresent(w -> w.foreman.Post(task));
    }
    public void SetEntityRelativeLocation(Entity entity, Vector2 pos) {
        SetEntityRelativeLocation(entity, pos.x, pos.y);
    }

    public Intersection LineTrace(float startX, float startY, float endX, float endY) {
        return LineTrace(new Vector2(startX, startY), new Vector2(endX, endY));
    }
    public Intersection LineTrace(Vector2 start, Vector2 end) {
        Intersection intersection = new Intersection();
        Trace((fixture, point, normal, fraction) -> {
            intersection.hit = true;
            intersection.point.set(point).scl(PhysicsSystem.physicsToScreen);
            intersection.normal.set(normal);
            return 0;
        }, start, end);

        List<Intersection> intersections = MultiLineTrace(start, end);
        if(intersections.isEmpty()) {
            System.out.println("Empty");
            return intersection;
        }
        return intersections.get(0);
    }

    public List<Intersection> MultiLineTrace(Vector2 start, Vector2 end) {
        List<Intersection> intersections = new ArrayList<>();
        Trace((fixture, point, normal, fraction) -> {
            Intersection intersection = new Intersection();
            intersection.hit = true;
            intersection.point.set(point).scl(PhysicsSystem.physicsToScreen);
            intersection.normal.set(normal);
            intersections.add(intersection);
            return 1;
        }, start, end);
        intersections.sort((i1, i2) -> Float.compare(i1.point.dst2(start), i2.point.dst2(start)));
        return intersections;
    }

    public void Trace(RayCastCallback callback, Vector2 start, Vector2 end) {
        collisionWorld.rayCast(callback, start.cpy().scl(PhysicsSystem.screenToPhysics), end.cpy().scl(PhysicsSystem.screenToPhysics));
    }

    public Vector2 GetMouseCoords() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public Vector2 UnprojectCoords(Entity entity, Vector2 screenCoords) {
        if(!entity.HasComponent(CameraComponent.class))
            return screenCoords;

        CameraComponent camera = entity.GetComponent(CameraComponent.class, true);
        Vector3 unproject = camera.camera.unproject(new Vector3(screenCoords, 0.f));
        return new Vector2(unproject.x, unproject.y);
    }

    public PhysicsContainer GetPhysicsContainer(String name) {
        return physicsSystem.GetContainer(name).orElse(null);
    }

    public void CreateCircularSensor(Vector2 point, float radius, Consumer<Body> task) {
        CreateCircularSensor(point, radius, BodyDef.BodyType.KinematicBody, task);
    }
    public void CreateCircularSensor(Vector2 point, float radius, BodyDef.BodyType type, Consumer<Body> task) {
        CreateCircularSensor("Overworld", point, radius, type, task);
    }
    public void CreateCircularSensor(String worldName, Vector2 point, float radius, BodyDef.BodyType type, Consumer<Body> task) {
        PhysicsSystem().GetContainer(worldName).ifPresent(c -> CreateCircularSensor(c, point, radius, type, task));
    }
    public void CreateCircularSensor(PhysicsContainer world, Vector2 point, float radius, BodyDef.BodyType type, Consumer<Body> task) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(point);
//        bodyDef.allowSleep = false;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef.shape = shape;

        fixtureDef.density = type.equals(BodyDef.BodyType.DynamicBody) ? 1f : 0f;

        SpawnEntityTask dispatchedTask = new SpawnEntityTask(bodyDef, fixtureDef);
        dispatchedTask.OnFinish(b -> {
            shape.dispose();
            if(task != null)
                task.accept(b);
        });
        world.foreman.Post(dispatchedTask);
    }

    public Body GetOtherBody(Contact contact, Body body) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        if(body.equals(bodyA))
            return bodyB;
        return bodyA;
    }

    public void DestroyBody(Body body) {
        PhysicsSystem().Foreman().Post(new DestroyBodyTask(body));
    }

    public void SetBodyTransformWS(Body body, Vector2 posWs, float angleDeg) {
        SetBodyTransform(body, posWs.cpy().scl(ScreenToPhysics()), (float) Math.toRadians(angleDeg));
    }

    public void SetBodyTransform(Body body, Vector2 posPS, float angleRad) {
        PhysicsSystem().Foreman().Post(new SetTransformTask(body, posPS, angleRad));
    }

    public Vector2 ToDirection(Vector2 a, Vector2 b) {
        return new Vector2(b.x - a.x, b.y - a.y).nor();
    }

    public boolean IsEntityAlive(Entity entity) {
        if(entity == null)
            return false;
        if(entity.HasComponent(HealthComponent.class)) {
            // TODO add support for multiple health components
            return entity.GetComponent(HealthComponent.class, true).health.health > 0;
        }
        return true;
    }

    public static class Intersection {
        public boolean hit;
        public final Vector2 point;
        public final Vector2 normal;

        public Intersection() {
            hit = false;
            point = new Vector2();
            normal = new Vector2();
        }
    }


    public static final List<Primitive> tempPrimitives = new ArrayList<>();

    public static abstract class Primitive {
        public float duration;
        public Color colour;

        public Primitive(float duration) {
            this.duration = duration;
        }

        public void _Render(ShapeRenderer renderer) {
            if(colour != null)
                renderer.setColor(colour);
            Render(renderer);
        }
        public abstract void Render(ShapeRenderer renderer);
    }

    public static class Point extends Primitive {

        public Point(float duration) {
            super(duration);
        }

        public final Vector2 point = new Vector2();

        @Override
        public void Render(ShapeRenderer renderer) {
            renderer.x(point, 4);
        }
    }

    public static class Circle extends Primitive {

        public Circle(float duration) {
            super(duration);
        }

        public float radius;
        public final Vector2 point = new Vector2();

        @Override
        public void Render(ShapeRenderer renderer) {
            renderer.circle(point.x, point.y, radius);
        }
    }

    public static class Line extends Primitive {

        public Line(float duration) {
            super(duration);
        }

        public final Vector2 start = new Vector2();
        public final Vector2 end = new Vector2();

        @Override
        public void Render(ShapeRenderer renderer) {
            renderer.line(start, end);
        }
    }

    public static class Rect extends Primitive {

        public Rect(float duration) {
            super(duration);
        }

        public final Vector2 pos = new Vector2();
        public final Vector2 size = new Vector2();

        @Override
        public void Render(ShapeRenderer renderer) {
            renderer.rect(pos.x, pos.y, size.x, size.y);
        }
    }

}

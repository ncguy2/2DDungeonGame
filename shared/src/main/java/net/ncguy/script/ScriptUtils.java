package net.ncguy.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;
import net.ncguy.entity.component.CameraComponent;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.world.Engine;

import java.util.ArrayList;
import java.util.List;

public class ScriptUtils {

    private static ScriptUtils instance;
    public static ScriptUtils instance() {
        if (instance == null)
            instance = new ScriptUtils();
        return instance;
    }

    private ScriptUtils() {
        ScriptHost.AddGlobalBinding("Utils", this);
    }

    protected Engine engine;
    protected World collisionWorld;

    public Engine Engine() { return engine; }
    public ScriptUtils Engine(Engine engine) {
        this.engine = engine;
        return this;
    }

    public World World() { return collisionWorld; }
    public ScriptUtils World(World collisionWorld) {
        this.collisionWorld = collisionWorld;
        return this;
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

    public void SetEntityRelativeLocation(Entity entity, float x, float y) {
        Transform2D transform = entity.Transform();
        transform.translation.set(x, y);
        CollisionComponent collision = entity.GetComponent(CollisionComponent.class, false);
        collision.body.setTransform(x * PhysicsSystem.screenToPhysics, y * PhysicsSystem.screenToPhysics, transform.RotationRad());
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

    public Vector2 ToDirection(Vector2 a, Vector2 b) {
        return new Vector2(b.x - a.x, b.y - a.y).nor();
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

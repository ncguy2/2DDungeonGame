package net.ncguy.world;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WindManager {

    public static WindManager Global = new WindManager();

    protected static Map<String, WindManager> instances = new HashMap<>();

    public static WindManager Get() {
        return Global;
    }
    public static WindManager Get(String name) {
        if(name == null || name.isEmpty())
            return Get();

        if (!instances.containsKey(name))
            instances.put(name, new WindManager());

        return instances.get(name);
    }

    protected BiConsumer<WindManager, Float> updateFuncRef;
    protected Vector2 wind;
    protected float strength;

    private WindManager() {
        wind = new Vector2().setToRandomDirection();
        strength = 1f;
    }

    public static void UpdateAll(final float delta) {
        Global.Update(delta);
        instances.values().forEach(e -> e.Update(delta));
    }

    public Vector2 CalculateTurbulence() {
        return new Vector2().setToRandomDirection();
    }
    public Vector2 GetWind() {
        return wind.cpy().scl(strength);
    }
    public Vector2 GetWind(float turbulenceFactor) {
        return CalculateTurbulence().scl(turbulenceFactor).add(GetWind());
    }

    public void Update(float delta) {
        if(updateFuncRef == null)
            updateFuncRef = WindManager::DefaultUpdate;
        updateFuncRef.accept(this, delta);
    }

    public static void DefaultUpdate(WindManager mgr, float delta) {
        float diff = (float) (Math.sin(delta) + 0.1);
        float angle = mgr.wind.angle();
        angle += diff;
        angle += 360;
        angle %= 360;
        mgr.wind.setAngle(angle);
        mgr.strength = 4f;
    }

}

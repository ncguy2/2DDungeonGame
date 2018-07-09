package net.ncguy.world;

import net.ncguy.system.BaseSystem;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    public final List<BaseSystem> systems;
    public final EntityWorld world;

    public Engine() {
        world = new EntityWorld();
        systems = new ArrayList<>();
    }

    public void Update(final float delta) {
        systems.forEach(s -> s.Update(delta));
        world.Update(delta);
    }

    public void AddSystem(BaseSystem system) {
        system.Startup();
        this.systems.add(system);
    }

    public void RemoveSystem(BaseSystem system) {
        this.systems.remove(system);
        system.Shutdown();
    }

}

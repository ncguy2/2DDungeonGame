package net.ncguy.world;

import net.ncguy.system.BaseSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Engine {

    public final List<BaseSystem> systems;

    public Engine() {
        systems = new ArrayList<>();
    }

    public void Update(final float delta) {
        systems.forEach(s -> s.Update(delta));
    }

    public void AddSystem(BaseSystem system) {
        system.SetOperatingEngine(this);
        system.Startup();
        this.systems.add(system);
    }

    public void RemoveSystem(BaseSystem system) {
        this.systems.remove(system);
        system.Shutdown();
    }


    // Helper
    public void IfIsMainEngine(Consumer<MainEngine> task) {}
}

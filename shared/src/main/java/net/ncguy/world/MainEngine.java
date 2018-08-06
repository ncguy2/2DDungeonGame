package net.ncguy.world;

import net.ncguy.profile.ProfilerHost;

import java.util.function.Consumer;

public class MainEngine extends Engine {

    public final EntityWorld world;

    public MainEngine() {
        world = new EntityWorld();
    }

    @Override
    public void Update(float delta) {
        ProfilerHost.Start("System update");
        super.Update(delta);
        ProfilerHost.End("System update");
        ProfilerHost.Start("World update");
        world.Update(delta);
        ProfilerHost.End("World update");
    }

    @Override
    public void IfIsMainEngine(Consumer<MainEngine> task) {
        task.accept(this);
    }
}

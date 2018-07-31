package net.ncguy.world;

import java.util.function.Consumer;

public class MainEngine extends Engine {

    public final EntityWorld world;

    public MainEngine() {
        world = new EntityWorld();
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        world.Update(delta);
    }

    @Override
    public void IfIsMainEngine(Consumer<MainEngine> task) {
        task.accept(this);
    }
}

package net.ncguy.system;

import net.ncguy.world.Engine;
import net.ncguy.world.EntityWorld;

public abstract class BaseSystem {

    protected final EntityWorld operatingWorld;
    protected Engine operatingEngine;

    public BaseSystem(EntityWorld operatingWorld) {
        this.operatingWorld = operatingWorld;
    }

    public void SetOperatingEngine(Engine engine) {
        this.operatingEngine = engine;
    }

    public abstract void Startup();
    public abstract void Update(float delta);
    public abstract void Shutdown();

}

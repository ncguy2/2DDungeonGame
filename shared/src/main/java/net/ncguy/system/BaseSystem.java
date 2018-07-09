package net.ncguy.system;

import net.ncguy.world.EntityWorld;

public abstract class BaseSystem {

    protected EntityWorld operatingWorld;

    public BaseSystem(EntityWorld operatingWorld) {
        this.operatingWorld = operatingWorld;
    }

    public abstract void Startup();
    public abstract void Update(float delta);
    public abstract void Shutdown();

}

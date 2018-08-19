package net.ncguy.server;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.GeneratorComponent;
import net.ncguy.lib.foundation.startup.Initialisation;
import net.ncguy.system.NetServerReplicateSystem;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.world.MainEngine;

/** Launches the server application. */
@SuppressWarnings("WeakerAccess")
public class ServerLauncher {

    public static final int tcpPort = 10000;
    public static final int udpPort = 10001;

    static MainEngine engine;
    static PhysicsSystem physics;

    public static void main(String[] args) {
        Initialisation.Init();
        engine = new MainEngine();
        physics = new PhysicsSystem(engine.world);
        engine.AddSystem(physics);
        engine.AddSystem(new NetServerReplicateSystem(engine.world, tcpPort, udpPort, engine));

        Entity mapEntity = new Entity();
        GeneratorComponent generator = mapEntity.AddComponent(new GeneratorComponent("Generator"));
        generator.generatorSeed = (int) System.currentTimeMillis();
//        generator.container = physics.GetContainer("Overworld").orElse(null);
        engine.world.Add(mapEntity);
        generator.Generate();
    }
}
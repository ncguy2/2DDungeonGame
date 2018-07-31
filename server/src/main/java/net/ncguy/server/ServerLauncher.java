package net.ncguy.server;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.GeneratorComponent;
import net.ncguy.lib.net.server.NetServer;
import net.ncguy.network.PacketClasses;
import net.ncguy.system.NetReplicateSystem;
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
        engine = new MainEngine();
        physics = new PhysicsSystem(engine.world);
        engine.AddSystem(physics);
        engine.AddSystem(new NetReplicateSystem(engine.world));

        Entity mapEntity = new Entity();
        GeneratorComponent generator = mapEntity.AddComponent(new GeneratorComponent("Generator"));
        generator.generatorSeed = (int) System.currentTimeMillis();
//        generator.container = physics.GetContainer("Overworld").orElse(null);
        engine.world.Add(mapEntity);
        generator.Generate();

        NetServer svr = new NetServer();
        svr.Start();
        svr.Register(PacketClasses.GetPacketClasses());
        svr.Bind(tcpPort, udpPort);
    }
}
package net.ncguy.system;

import net.ncguy.lib.net.server.NetServer;
import net.ncguy.network.PacketClasses;
import net.ncguy.world.EntityWorld;
import net.ncguy.world.MainEngine;

public class NetServerReplicateSystem extends NetReplicateSystem<NetServer> {

    public NetServerReplicateSystem(EntityWorld operatingWorld, int tcpPort, int udpPort, MainEngine engine) {
        super(operatingWorld, tcpPort, udpPort, engine);
    }

    @Override
    protected void InitEndpoint() {
        endPoint = new NetServer();
        endPoint.Start();
        endPoint.Register(PacketClasses.GetPacketClasses());
        endPoint.Bind(tcpPort, udpPort);
    }
}

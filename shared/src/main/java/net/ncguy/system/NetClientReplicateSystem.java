package net.ncguy.system;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.lib.net.client.NetClient;
import net.ncguy.lib.net.shared.NetListener;
import net.ncguy.network.PacketClasses;
import net.ncguy.network.packet.WorldRequestPacket;
import net.ncguy.world.EntityWorld;
import net.ncguy.world.MainEngine;

public class NetClientReplicateSystem extends NetReplicateSystem<NetClient> {

    String host;

    public NetClientReplicateSystem(EntityWorld operatingWorld, String host, int tcpPort, int udpPort, MainEngine engine) {
        super(operatingWorld, tcpPort, udpPort, engine);
        this.host = host;
    }

    @Override
    protected void InitEndpoint() {
        endPoint = new NetClient() {
            @Override
            public void BindDefaultListener() {
                endpoint.addListener(new NetListener(GetSide(), this) {
                    @Override
                    public void connected(Connection connection) {
                        connection.sendTCP(new WorldRequestPacket());
                    }
                });
            }
        };
        endPoint.Register(PacketClasses.GetPacketClasses());
        endPoint.Start();
        endPoint.Connect(host, tcpPort, udpPort);
    }
}

package net.ncguy.lib.net.server;

import com.esotericsoftware.kryonet.Server;
import net.ncguy.lib.net.shared.NetEndpoint;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

import java.io.IOException;

public class NetServer extends NetEndpoint<Server> {

    public NetServer() {
        endpoint = new Server();
        BindDefaultListener();
    }

    public void Bind(int tcpPort) {
        try {
            BindImpl(tcpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void BindImpl(int tcpPort) throws IOException {
        endpoint.bind(tcpPort);
    }

    public void Bind(int tcpPort, int udpPort) {
        try {
            BindImpl(tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void BindImpl(int tcpPort, int udpPort) throws IOException {
        endpoint.bind(tcpPort, udpPort);
    }

    @Override
    public PacketReceivedHandler.Side GetSide() {
        return PacketReceivedHandler.Side.Server;
    }
}

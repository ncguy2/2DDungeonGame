package net.ncguy.lib.net.client;

import com.esotericsoftware.kryonet.Client;
import net.ncguy.lib.net.shared.NetEndpoint;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

import java.io.IOException;

public class NetClient extends NetEndpoint<Client> {

    public int timeout = 5000;

    public NetClient() {
        endpoint = new Client();
        BindDefaultListener();
    }

    @Override
    public PacketReceivedHandler.Side GetSide() {
        return PacketReceivedHandler.Side.Client;
    }

    public void Connect(String hostname, int port) {
        try {
            ConnectImpl(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ConnectImpl(String hostname, int port) throws IOException {
        endpoint.connect(timeout, hostname, port);
    }

    public void Connect(String hostname, int tcpPort, int udpPort) {
        try {
            ConnectImpl(hostname, tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ConnectImpl(String hostname, int tcpPort, int udpPort) throws IOException {
        endpoint.connect(timeout, hostname, tcpPort, udpPort);
    }

    public void SendTCP(Object object) {
        endpoint.sendTCP(object);
    }
    public void SendUDP(Object object) {
        endpoint.sendUDP(object);
    }

    public void Send(Object object, boolean reliable) {
        if(reliable)
            SendTCP(object);
        else SendUDP(object);
    }

}

package net.ncguy.server;

import net.ncguy.lib.net.server.NetServer;
import net.ncguy.network.PacketClasses;

/** Launches the server application. */
@SuppressWarnings("WeakerAccess")
public class ServerLauncher {

    public static final int tcpPort = 10000;
    public static final int udpPort = 10001;

    public static void main(String[] args) {
        NetServer svr = new NetServer();
        svr.Start();
        svr.Register(PacketClasses.GetPacketClasses());
        svr.Bind(tcpPort, udpPort);
    }
}
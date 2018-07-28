package net.ncguy.lib.net.test;

import net.ncguy.lib.net.Network;
import net.ncguy.lib.net.server.NetServer;

public class DemoServer {

    public static void main(String[] args) {
        NetServer svr = new NetServer();
        svr.Start();
        svr.Register(Network.GetPacketClasses());
        svr.Bind(10000, 10001);
    }

}

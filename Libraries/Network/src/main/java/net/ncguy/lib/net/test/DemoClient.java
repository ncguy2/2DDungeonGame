package net.ncguy.lib.net.test;

import net.ncguy.lib.net.Network;
import net.ncguy.lib.net.client.NetClient;
import net.ncguy.lib.net.packets.JsonPacket;

public class DemoClient {

    public static void main(String[] args) {
        NetClient cli = new NetClient();
        cli.Start();
        cli.Register(Network.GetPacketClasses());
        cli.Connect("127.0.0.1", 10000, 10001);

        JsonPacket pkt = new JsonPacket();
        pkt.typeClassPath = JsonPacket.class.getCanonicalName();
        pkt.json = "This isn't actual JSON";

        cli.SendTCP(pkt);

    }

}

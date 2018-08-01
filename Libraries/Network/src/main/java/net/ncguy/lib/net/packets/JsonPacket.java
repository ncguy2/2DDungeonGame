package net.ncguy.lib.net.packets;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.lib.net.shared.NetEndpoint;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

@PacketInfo(Name = "Json packet", Handler = JsonPacket.JsonPacketHandler.class)
public class JsonPacket {

    public String typeClassPath;
    public String json;

    public static class JsonPacketHandler extends PacketReceivedHandler<JsonPacket> {
        @Override
        public void Handle(NetEndpoint endpoint, Connection source, Side side, JsonPacket packet) {
            System.out.println("Json packet received");
            System.out.println("\t" + packet.typeClassPath);
            System.out.println("\t" + packet.json);
        }
    }

}

package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

@PacketInfo(Name = "World packet", Handler = WorldRequestPacket.WorldRequestPacketHandler.class)
public class WorldRequestPacket extends DungeonPacket {

    public static class WorldRequestPacketHandler extends PacketReceivedHandler<WorldRequestPacket> {
        @Override
        public void Handle(Connection source, Side side, WorldRequestPacket packet) {
            if(side != Side.Server) return;

            packet.engine.IfIsMainEngine(e -> {
                WorldPacket pkt = WorldPacket.BuildFromWorld(e.world);
                source.sendTCP(pkt);
            });

        }
    }

}

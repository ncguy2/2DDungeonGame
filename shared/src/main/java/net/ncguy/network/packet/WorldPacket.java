package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.entity.Entity;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

import java.util.List;

@PacketInfo(Name = "World packet", Handler = WorldPacket.WorldPacketHandler.class)
public class WorldPacket {

    public List<Entity> entities;

    public static class WorldPacketHandler extends PacketReceivedHandler<WorldPacket> {

        @Override
        public void Handle(Connection source, Side side, WorldPacket packet) {
            if(packet.entities == null) return;
            // TODO interface with local world
        }
    }

}

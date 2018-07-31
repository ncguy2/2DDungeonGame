package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;
import net.ncguy.world.Engine;

import java.util.UUID;

@PacketInfo(Name = "Base dungeon packet", Handler = DungeonPacket.DungeonPacketHandler.class)
public abstract class DungeonPacket {

    public final UUID packetUUID;

    public transient Engine engine;

    public DungeonPacket() {
        this.packetUUID = UUID.randomUUID();
    }

    public static class DungeonPacketHandler extends PacketReceivedHandler<DungeonPacket> {

        public Engine engine;

        public DungeonPacketHandler() {
            this.type = DungeonPacket.class;
        }

        @Override
        public void Handle(Connection source, Side side, DungeonPacket packet) {
            packet.engine = this.engine;
        }
    }
}

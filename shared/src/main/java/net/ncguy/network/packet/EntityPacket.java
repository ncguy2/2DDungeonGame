package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.entity.Entity;
import net.ncguy.lib.net.server.NetServer;
import net.ncguy.lib.net.shared.NetEndpoint;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

@PacketInfo(Name = "Entity packet", Handler = EntityPacket.EntityPacketHandler.class)
public class EntityPacket extends DungeonPacket {

    public Entity entity;

    public EntityPacket() {}

    public EntityPacket(Entity entity) {
        this.entity = entity;
    }

    public static class EntityPacketHandler extends PacketReceivedHandler<EntityPacket> {
        @Override
        public void Handle(NetEndpoint endpoint, Connection source, Side side, EntityPacket packet) {
            if(packet.entity == null) return;
            packet.engine.IfIsMainEngine(e -> e.world.AddUniqueEntity(packet.entity));
            if(side.equals(Side.Server))
                ((NetServer) endpoint).SendTCPExclude(source.getID(), packet);
        }
    }
}

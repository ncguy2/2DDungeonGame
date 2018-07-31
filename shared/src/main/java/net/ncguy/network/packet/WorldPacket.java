package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;
import net.ncguy.world.EntityWorld;

import java.util.List;

@PacketInfo(Name = "World packet", Handler = WorldPacket.WorldPacketHandler.class)
public class WorldPacket extends DungeonPacket {

    public List<Entity> entities;

    public static WorldPacket BuildFromWorld(EntityWorld world) {
        WorldPacket pkt = new WorldPacket();
        pkt.entities = world.getEntities();
        return pkt;
    }

    public static class WorldPacketHandler extends PacketReceivedHandler<WorldPacket> {

        @Override
        public void Handle(Connection source, Side side, WorldPacket packet) {
            if (packet.entities == null) return;
            packet.engine.IfIsMainEngine(e -> {
                e.world.AddUniqueEntities(packet.entities);
                packet.entities.forEach(ent -> {
                    ent.GetComponents(EntityComponent.class, true)
                            .forEach(EntityComponent::PostReplicate);
                });

                e.world.FlattenedEntities().stream().filter(Entity::IsManaged).map(EntityPacket::new).forEach(source::sendTCP);
            });
        }
    }

}

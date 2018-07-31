package net.ncguy.system;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.entity.component.net.ReplicateComponent;
import net.ncguy.lib.net.shared.PacketReceivedHandler;
import net.ncguy.network.packet.DungeonPacket;
import net.ncguy.network.packet.TransformPacket;
import net.ncguy.world.EntityWorld;

import java.util.List;

public class NetReplicateSystem extends BaseSystem {

    public NetReplicateSystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {
        PacketReceivedHandler.Get(DungeonPacket.class)
                .filter(e -> e instanceof DungeonPacket.DungeonPacketHandler)
                .map(e -> (DungeonPacket.DungeonPacketHandler) e)
                .ifPresent(e -> e.engine = operatingEngine);
    }

    @Override
    public void Update(float delta) {
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(ReplicateComponent.class);

        entities.stream()
                .map(e -> e.GetComponent(ReplicateComponent.class, true))
                .filter(ReplicateComponent::IsReplicationDue)
                .map(ReplicateComponent::Replication)
                .map(EntityComponent::GetOwningComponent)
                .forEach(this::Accept);
    }

    public void Accept(SceneComponent component) {
        TransformPacket pkt = new TransformPacket();
        pkt.fullPath = component.GetFullPath();
        pkt.newTransform = component.transform;
        // TODO send transform packet
    }

    @Override
    public void Shutdown() {

    }
}

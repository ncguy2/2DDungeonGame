package net.ncguy.system;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.entity.component.net.ReplicateComponent;
import net.ncguy.lib.net.shared.NetEndpoint;
import net.ncguy.lib.net.shared.PacketReceivedHandler;
import net.ncguy.network.packet.DungeonPacket;
import net.ncguy.network.packet.TransformPacket;
import net.ncguy.world.EntityWorld;
import net.ncguy.world.MainEngine;

import java.util.List;

public abstract class NetReplicateSystem<T extends NetEndpoint<?>> extends BaseSystem {

    int tcpPort;
    int udpPort;
    private final MainEngine engine;
    T endPoint;

    public NetReplicateSystem(EntityWorld operatingWorld, int tcpPort, int udpPort, MainEngine engine) {
        super(operatingWorld);
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.engine = engine;
    }

    protected abstract void InitEndpoint();

    @Override
    public void Startup() {
        PacketReceivedHandler.Get(DungeonPacket.class)
                .filter(e -> e instanceof DungeonPacket.DungeonPacketHandler)
                .map(e -> (DungeonPacket.DungeonPacketHandler) e)
                .ifPresent(e -> e.engine = engine);
        InitEndpoint();
    }

    @Override
    public void Update(float delta) {
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(ReplicateComponent.class);

        entities.stream()
                .filter(Entity::IsManaged)
                .map(e -> e.GetComponent(ReplicateComponent.class, true))
                .filter(ReplicateComponent::IsReplicationDue)
                .map(ReplicateComponent::Replication)
                .map(EntityComponent::GetOwningComponent)
                .forEach(this::Accept);
    }

    public void Accept(SceneComponent component) {
        TransformPacket pkt = new TransformPacket();
        pkt.fullPath = component.GetFullPath();
        pkt.newTransform = component.transform.Predict(0.016666668f).Copy();
        endPoint.SendUDP(pkt);
    }

    @Override
    public void Shutdown() {

    }
}

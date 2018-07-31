package net.ncguy.network.packet;

import com.esotericsoftware.kryonet.Connection;
import net.ncguy.entity.Transform2D;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.lib.net.shared.PacketInfo;
import net.ncguy.lib.net.shared.PacketReceivedHandler;

@PacketInfo(Name = "Transform packet", Handler = TransformPacket.TransformPacketHandler.class)
public class TransformPacket extends DungeonPacket {

    public String fullPath;
    public Transform2D newTransform;

    public static class TransformPacketHandler extends PacketReceivedHandler<TransformPacket> {

        @Override
        public void Handle(Connection source, Side side, TransformPacket packet) {
            if(packet.fullPath == null) return;
            if(packet.newTransform == null) return;

//            String playerPath = playerEntity.uuid + "://Collision/Camera arm/Camera";
//            EntityComponent entityComponent = engine.world.GetFromPath(playerPath);

            packet.engine.IfIsMainEngine(e -> {
                EntityComponent ec = e.world.GetFromPath(packet.fullPath);
                if(ec instanceof SceneComponent)
                    ((SceneComponent) ec).transform.Set(packet.newTransform);
            });


        }
    }

}

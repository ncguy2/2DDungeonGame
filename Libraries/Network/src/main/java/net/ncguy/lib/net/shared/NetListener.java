package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import net.ncguy.lib.net.utils.Sorters;

public class NetListener extends Listener {

    public final PacketReceivedHandler.Side side;
    private final NetEndpoint endPoint;

    public NetListener(PacketReceivedHandler.Side side, NetEndpoint endPoint) {
        super();
        this.side = side;
        this.endPoint = endPoint;
    }

    @Override
    public void received(Connection connection, Object object) {
        PacketReceivedHandler.GetAllForType(object.getClass())
                .stream()
                .sorted(Sorters.ClassHierarchyDescent(PacketReceivedHandler::GetType))
                .forEach(h -> h.HandleObject(endPoint, connection, side, object));
    }
}
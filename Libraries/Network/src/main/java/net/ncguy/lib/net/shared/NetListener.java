package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class NetListener extends Listener {

    public final PacketReceivedHandler.Side side;
    public NetListener(PacketReceivedHandler.Side side) {
        super();
        this.side = side;
    }

    @Override
    public void received(Connection connection, Object object) {
        PacketReceivedHandler.Get(object.getClass())
                .ifPresent(h -> h.HandleObject(connection, side, object));
    }
}
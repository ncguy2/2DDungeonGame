package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import net.ncguy.lib.net.Network;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class NetEndpoint<T extends EndPoint> {

    protected T endpoint;

    protected int writeBufferSize = 16384 * 4;
    protected int objectBufferSize = 16384 * 4;

    public void BindDefaultListener() {
        endpoint.addListener(new NetListener(GetSide(), this));
    }

    public abstract PacketReceivedHandler.Side GetSide();

    public void Start() {
        endpoint.start();
    }

    public void Stop() {
        endpoint.stop();
    }

    public void AddListener(Listener listener) {
        endpoint.addListener(listener);
    }

    public void RemoveListener(Listener listener) {
        endpoint.removeListener(listener);
    }

    public void Register(List<Network.Entry> classes) {
        Kryo kryo = endpoint.getKryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.addDefaultSerializer(Collection.class, ConfigurableElementSerializer.class);
        kryo.addDefaultSerializer(Set.class, ConfigurableElementSerializer.class);
//        Log.TRACE();
        classes.forEach(e -> e.Register(kryo));
    }

    public T GetInternalEndpoint() {
        return endpoint;
    }

    public abstract void SendTCP(Object pkt);
    public abstract void SendUDP(Object pkt);
}

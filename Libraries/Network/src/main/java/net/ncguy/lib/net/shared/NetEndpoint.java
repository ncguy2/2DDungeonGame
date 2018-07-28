package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import java.util.List;

public abstract class NetEndpoint<T extends EndPoint> {

    protected T endpoint;

    public void BindDefaultListener() {
        endpoint.addListener(new NetListener(GetSide()));
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

    public void Register(List<Class> classes) {
        Kryo kryo = endpoint.getKryo();
        classes.forEach(kryo::register);
    }

}

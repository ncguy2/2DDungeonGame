package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryonet.Connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class PacketReceivedHandler<T> {

    static Map<Class, PacketReceivedHandler> handlers = new HashMap<>();

    public static <T> Optional<PacketReceivedHandler<T>> Get(Class<T> type) {
        try {
            return Optional.ofNullable(GetImpl(type));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T> PacketReceivedHandler<T> GetImpl(Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (handlers.containsKey(type))
            return handlers.get(type);

        if (!type.isAnnotationPresent(PacketInfo.class))
            return null;

        PacketInfo pktInfo = type.getAnnotation(PacketInfo.class);
        if (pktInfo.Handler()
                .equals(PacketReceivedHandler.class))
            return null;

        Constructor<? extends PacketReceivedHandler> ctor = pktInfo.Handler()
                .getConstructor();
        PacketReceivedHandler<T> handler = ctor.newInstance();
        handlers.put(type, handler);
        return handler;

    }

    public void HandleObject(Connection source, Side side, Object obj) {
        Handle(source, side, (T) obj);
    }
    public abstract void Handle(Connection source, Side side, T packet);

    public static enum Side {
        Server,
        Client,
        ;
    }

}

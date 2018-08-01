package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryonet.Connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class PacketReceivedHandler<T> {

    static Map<Class, PacketReceivedHandler> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static List<PacketReceivedHandler> GetAllForType(Class type) {
        List<PacketReceivedHandler> handlers = new ArrayList<>();
        GetAllForType(type, handlers);
        return handlers;
    }

    public static <T> void GetAllForType(Class<T> type, List<PacketReceivedHandler> handlers) {

        Get(type).ifPresent(handlers::add);

        if(!type.getSuperclass().equals(Object.class))
            GetAllForType(type.getSuperclass(), handlers);
    }

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
        handler.type = type;
        handlers.put(type, handler);
        return handler;
    }

    public void HandleObject(NetEndpoint endpoint, Connection source, Side side, Object obj) {
        Handle(endpoint, source, side, (T) obj);
    }
    public abstract void Handle(NetEndpoint endpoint, Connection source, Side side, T packet);
    public Class type;
    public Class GetType() {
        return type;
    }

    public static enum Side {
        Server,
        Client,
        ;
    }

}

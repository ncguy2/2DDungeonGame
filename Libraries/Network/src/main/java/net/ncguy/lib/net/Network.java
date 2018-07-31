package net.ncguy.lib.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import net.ncguy.lib.net.packets.JsonPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Network {

    public static List<Entry> GetPacketClasses() {
        List<Entry> clsList = new ArrayList<>();

        clsList.add(new Entry(JsonPacket.class));
        clsList.add(new Entry(ArrayList.class));
        clsList.add(new Entry(Object.class));
        clsList.add(new Entry(List.class));
        clsList.add(new Entry(HashSet.class));
        clsList.add(new Entry(LinkedHashSet.class));
        clsList.add(new Entry(float[].class));

        return clsList;
    }

    public static class Entry<T> {
        public Class<T> cls;
        public Serializer<T> serializer;

        public Entry(Class<T> cls) {
            this.cls = cls;
        }

        public Entry(Class<T> cls, Serializer<T> serializer) {
            this.cls = cls;
            this.serializer = serializer;
        }

        public void Register(Kryo kryo) {
            if(serializer != null)
                kryo.register(cls, serializer);
            else kryo.register(cls);
        }

    }

}

package net.ncguy.lib.net;

import net.ncguy.lib.net.packets.JsonPacket;

import java.util.ArrayList;
import java.util.List;

public class Network {

    public static List<Class> GetPacketClasses() {
        List<Class> clsList = new ArrayList<>();

        clsList.add(JsonPacket.class);

        return clsList;
    }

}

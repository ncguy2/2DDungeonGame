package net.ncguy.network;

import net.ncguy.lib.net.Network;

import java.util.ArrayList;
import java.util.List;

public class PacketClasses {

    public static List<Class> GetPacketClasses() {
        List<Class> list = new ArrayList<>(Network.GetPacketClasses());



        return list;
    }

}

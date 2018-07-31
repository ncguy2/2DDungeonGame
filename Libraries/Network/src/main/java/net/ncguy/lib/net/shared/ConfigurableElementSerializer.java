package net.ncguy.lib.net.shared;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurableElementSerializer extends CollectionSerializer {

    @Override
    public void write(Kryo kryo, Output output, Collection collection) {

        List nonReplicatingComponents = (List) collection.stream()
                .filter(e -> IReplicationConfigurable.class.isInstance(e))
                .filter(e -> !((IReplicationConfigurable) e).CanReplicate())
                .collect(Collectors.toList());

        ArrayList replicatingComponents = new ArrayList<>(collection);
        replicatingComponents.removeAll(nonReplicatingComponents);

        super.write(kryo, output, replicatingComponents);
    }
}

package net.ncguy.entity.component;

import java.util.ArrayList;
import java.util.Collection;

public interface IPropertyProvider {

    default Collection<FieldPropertyDescriptorLite> Provide() {
        ArrayList<FieldPropertyDescriptorLite> descriptors = new ArrayList<>();
        Provide(descriptors);
        return descriptors;
    }

    void Provide(Collection<FieldPropertyDescriptorLite> descriptors);

}

package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.collections.ObservableList;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.post.MultiPostProcessor;
import net.ncguy.tools.debug.view.component.FieldPropertyDescriptor;
import org.controlsfx.control.PropertySheet;

import java.util.Collection;
import java.util.function.Function;

public class MultiPostProcessorItem extends PropertySheet {

    private final MultiPostProcessor processor;

    public MultiPostProcessorItem(MultiPostProcessor processor) {
        super();
        this.processor = processor;
        Build();
    }

    void Build() {
        Collection<FieldPropertyDescriptorLite> descriptors = processor.Provide();
        ObservableList<Item> items = getItems();
        items.clear();
        descriptors.stream()
                .map((Function<FieldPropertyDescriptorLite, FieldPropertyDescriptor>) FieldPropertyDescriptor::new)
                .forEach(items::add);
    }

}

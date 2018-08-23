package net.ncguy.tools.debug.view.component.editors.nodes;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;

import java.util.List;

public abstract class CollectionEditor<T> extends Accordion {

    private final ObservableList<T> list;

    public CollectionEditor(List<T> list) {
        super();
        this.list = new ObservableListWrapper<>(list);
        Populate();
        RegisterListeners();
    }

    void Populate() {
        getPanes().clear();
        this.list.stream().map(this::Build).forEach(getPanes()::add);
    }

    void RegisterListeners() {
        this.list.addListener((ListChangeListener<? super T>) c -> Populate());
    }

    protected abstract Node BuildNode(T item);

    CollectionItem Build(T item) {
        Node content = BuildNode(item);
        return new CollectionItem(item.getClass().getSimpleName(), content);
    }

}

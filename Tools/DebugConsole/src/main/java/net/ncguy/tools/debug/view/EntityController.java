package net.ncguy.tools.debug.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.world.EntityWorld;

import java.net.URL;
import java.util.ResourceBundle;

public class EntityController implements Initializable {

    EntityWorld focusedWorld;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        focusedWorld = EntityWorld.instance;
        Rebuild();
    }

    boolean Valid() {
        if(focusedWorld != null)
            return true;
        focusedWorld = EntityWorld.instance;
        return focusedWorld != null;
    }

    public void Rebuild() {
        if(!Valid()) return;
        TreeItem<Entity> item = new TreeItem<>(null);
        for (Entity entity : focusedWorld.getEntities())
            Build(item, entity);
        item.setExpanded(true);
        Container.setRoot(item);
    }

    public void Select(Entity entity) {
        if(!Valid()) return;
        if(entity == null) return;

        TreeItem<EntityComponent> item = Build(null, entity.GetRootComponent());
        ComponentContainer.setRoot(item);
    }

    public void Build(TreeItem<Entity> parent, Entity entity) {
        TreeItem<Entity> item = new TreeItem<>(entity);
        parent.getChildren().add(item);

        for (Entity childEntity : entity.childEntities)
            Build(item, childEntity);
    }

    public TreeItem<EntityComponent> Build(TreeItem<EntityComponent> parent, EntityComponent component) {
        TreeItem<EntityComponent> item = new TreeItem<>(component);
        if(parent != null)
            parent.getChildren().add(item);

        item.setExpanded(true);

        if(component instanceof SceneComponent)
            ((SceneComponent) component).childrenComponents.forEach(childComponent -> Build(item, childComponent));
        return item;
    }

    @FXML
    private TreeView<Entity> Container;
    @FXML
    private TreeView<EntityComponent> ComponentContainer;

    public void EntitySelected(MouseEvent mouseEvent) {
        TreeItem<Entity> item = Container.getSelectionModel()
                .getSelectedItem();
        if(item == null)
            return;
        Select(item.getValue());
    }
}

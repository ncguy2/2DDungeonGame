package net.ncguy.tools.debug.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.tools.debug.view.component.FieldPropertyDescriptor;
import net.ncguy.tools.debug.view.component.FieldPropertyFactory;
import net.ncguy.world.EntityWorld;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EntityController implements Initializable {

    EntityWorld focusedWorld;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        focusedWorld = EntityWorld.instance;

        Container.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                Select(newValue.getValue());
        });

        Callback<TreeView<Entity>, TreeCell<Entity>> cellFactory = tv -> {
            TreeCell<Entity> cell = new TreeCell<Entity>() {
                @Override
                protected void updateItem(Entity item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle(null);
                    } else {
                        setText(item.toString());
                        if(item.IsManaged())
                            setStyle("-fx-background-color: #00c3c363;");
                        else setStyle(null);
                    }
                }
            };
            return cell;
        };
        Container.setCellFactory(cellFactory);

        ComponentContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                Select(newValue.getValue());
        });

        Rebuild();
    }

    private void Select(EntityComponent value) {
        ObservableList<PropertySheet.Item> items = ComponentDetails.getItems();
        Callback<PropertySheet.Item, PropertyEditor<?>> factory = ComponentDetails.getPropertyEditorFactory();

        items.clear();
        if(value == null)
            return;
        List<FieldPropertyDescriptor> propertyDescriptors = FieldPropertyFactory.GetDescriptors(value);
        items.setAll(propertyDescriptors);
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

        Select(entity.GetRootComponent());
        ComponentContainer.getSelectionModel().select(0);
    }

    public void Build(TreeItem<Entity> parent, Entity entity) {
        TreeItem<Entity> item = new TreeItem<>(entity);
        parent.getChildren().add(item);

        for (Entity childEntity : entity.GetChildren())
            Build(item, childEntity);
    }

    public TreeItem<EntityComponent> Build(TreeItem<EntityComponent> parent, EntityComponent component) {
        TreeItem<EntityComponent> item = new TreeItem<>(component);
        if(parent != null)
            parent.getChildren().add(item);

        item.setExpanded(true);

        if(component instanceof SceneComponent)
            ((SceneComponent) component).GetComponents().forEach(childComponent -> Build(item, childComponent));
        return item;
    }

    @FXML
    private TreeView<Entity> Container;
    @FXML
    private TreeView<EntityComponent> ComponentContainer;
    @FXML
    private PropertySheet ComponentDetails;

}

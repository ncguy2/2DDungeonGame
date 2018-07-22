package net.ncguy.tools.debug.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import net.ncguy.ability.Ability;
import net.ncguy.ability.AbilityRegistry;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AbilityController implements Initializable {


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Ability> all = AbilityRegistry.instance().All();
    }

    @FXML
    public AnchorPane AbilityContainer;
    @FXML
    public ListView<Ability> Container;

    public void AbilitySelected(MouseEvent mouseEvent) {

    }
}

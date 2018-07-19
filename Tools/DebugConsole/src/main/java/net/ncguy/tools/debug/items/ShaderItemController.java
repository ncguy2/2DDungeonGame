package net.ncguy.tools.debug.items;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import net.ncguy.util.ReloadableShader;

import java.net.URL;
import java.util.ResourceBundle;

public class ShaderItemController implements Initializable {

    private ReloadableShader shader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void SetShader(ReloadableShader shader) {
        this.shader = shader;
        shaderName.setText(shader.Name());
    }

    @FXML
    public Label shaderName;

    public void RecompileShader(ActionEvent actionEvent) {
        if(shader != null)
            shader.Reload();
    }
}

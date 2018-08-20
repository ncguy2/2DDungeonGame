package net.ncguy.tools.debug.items;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import net.ncguy.particles.ParticleProfile;

import java.net.URL;
import java.util.ResourceBundle;

public class ParticleItemController implements Initializable {

    public VBox blockContainer;
    public TitledPane pane;

    ParticleProfile profile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Update();
    }

    public void SetProfile(ParticleProfile profile) {
        this.profile = profile;
        Update();
    }

    public void Update() {
        if(profile == null) {
            this.pane.setText("Null profile");
            return;
        }

        this.pane.setText(profile.name);
        for (String block : profile.blocks) {
            this.blockContainer.getChildren().add(new Label(block));
        }
    }


    public ParticleProfile GetProfile() {
        return this.profile;
    }
}
